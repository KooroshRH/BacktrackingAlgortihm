import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Main
{
    public static class Block implements Comparable<Block>
    {
        public int y;
        public int x;
        public String color;
        public int number;
        public Block[][] board;
        public ArrayList<Integer> numbersDomain = new ArrayList<>();
        public ArrayList<String> colorsDomain = new ArrayList<>();

        @Override
        public int compareTo(Block o) {
            if (colorsDomain.size() + numbersDomain.size() != o.colorsDomain.size() + o.numbersDomain.size())
            {
                return colorsDomain.size() + numbersDomain.size() - o.colorsDomain.size() + o.numbersDomain.size();
            }
            else
            {
                return getDegree() - o.getDegree();
            }
        }

        public int getDegree()
        {
            int degree = 0;
            for (int i = 0; i < boardSize; i++)
            {
                if (board[y][i].number == 0 && i != x)
                {
                    degree++;
                }
            }
            for (int i = 0; i < boardSize; i++)
            {
                if (board[i][x].number == 0 && i != y)
                {
                    degree++;
                }
            }
            if (y + 1 != boardSize && board[y+1][x].color == null)
            {
                degree++;
            }
            if (x + 1 != boardSize && board[y][x+1].color == null)
            {
                degree++;
            }
            if (y - 1 != -1 && board[y-1][x].color == null)
            {
                degree++;
            }
            if (x - 1 != -1 && board[y][x-1].color == null)
            {
                degree++;
            }
            return degree;
        }
    }

    public static class State implements Comparable<State>
    {
        public State parent;
        public Block[][] board;

        @Override
        public int compareTo(State o) {
            return getChangedBlockWithParent(this).compareTo(getChangedBlockWithParent(o));
        }
    }

    public static ArrayList<String> colors;
    public static int boardSize;
    public static int colorCount;
    public static int childrenCount = 0;
    public static Scanner scanner;

    public static void main(String[] args)
    {
        scanner = new Scanner(System.in);
        colorCount = scanner.nextInt();
        boardSize = scanner.nextInt();
        colors = new ArrayList<>();
        scanner.nextLine();
        String[] colorsString = scanner.nextLine().split(" ");
        colors.addAll(Arrays.asList(colorsString).subList(0, colorCount));

        State root = new State();
        root.parent = null;
        Block[][] rootBoard = makeRootStateFromInput();
        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++) forwardChecking(rootBoard, i, j);
        root.board = rootBoard;
        State res = backwardSearch(root);
        if (res != null)
        {
            printState(res);
        }
        else
        {
            System.out.println("Impossible!");
        }
    }

    private static Block[][] makeRootStateFromInput() {
        Block[][] rootBoard = new Block[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++)
        {
            String[] boardRow = scanner.nextLine().split(" ");
            for (int j = 0; j < boardSize; j++)
            {
                String BlockString = boardRow[j];
                Block newBlock = new Block();
                if (BlockString.toCharArray()[0] == '*')
                {
                    newBlock.number = 0;
                }
                else
                {
                    newBlock.number = Integer.parseInt(BlockString.substring(0, 1));
                }
                newBlock.color = BlockString.substring(1);
                if (newBlock.color.equals("#"))
                {
                    newBlock.color = null;
                }
                newBlock.colorsDomain = new ArrayList<>(colors);
                Collections.reverse(newBlock.colorsDomain);
                for (int k = 1; k <= boardSize; k++)
                {
                    newBlock.numbersDomain.add(k);
                }
                newBlock.board = rootBoard;
                rootBoard[i][j] = newBlock;
            }
        }
        return rootBoard;
    }

    private static void forwardChecking(Block[][] board, int targetY, int targetX)
    {
        if (board[targetY][targetX].number != 0)
        {
            for (int k = 0; k < boardSize; k++)
            {
                board[targetY][k].numbersDomain.remove((Integer)board[targetY][targetX].number);
            }
            for (int k = 0; k < boardSize; k++)
            {
                board[k][targetX].numbersDomain.remove((Integer)board[targetY][targetX].number);
            }
        }
        if (board[targetY][targetX].color != null)
        {
            if (targetY + 1 != boardSize) {
                board[targetY + 1][targetX].colorsDomain.remove(board[targetY][targetX].color);
            }
            if (targetX + 1 != boardSize) {
                board[targetY][targetX + 1].colorsDomain.remove(board[targetY][targetX].color);
            }
            if (targetY - 1 != -1) {
                board[targetY - 1][targetX].colorsDomain.remove(board[targetY][targetX].color);
            }
            if (targetX - 1 != -1) {
                board[targetY][targetX - 1].colorsDomain.remove(board[targetY][targetX].color);
            }
            if (board[targetY][targetX].number != 0)
            {
                if (targetY+1 != boardSize)
                {
                    if (board[targetY+1][targetX].color != null) {
                        if (colors.indexOf(board[targetY][targetX].color) > colors.indexOf(board[targetY + 1][targetX].color)) {
                            for (int m = 1; m <= board[targetY][targetX].number; m++) {
                                board[targetY + 1][targetX].numbersDomain.remove((Integer) m);
                            }
                        } else {
                            for (int m = board[targetY][targetX].number; m <= boardSize; m++) {
                                board[targetY + 1][targetX].numbersDomain.remove((Integer) m);
                            }
                        }
                    } else if(board[targetY+1][targetX].number != 0) {
                        if (board[targetY][targetX].number > board[targetY+1][targetX].number)
                        {
                            for (int m = 0; m <= colors.indexOf(board[targetY][targetX].color); m++)
                            {
                                board[targetY+1][targetX].colorsDomain.remove(colors.get(m));
                            }
                        } else {
                            for (int m = colors.indexOf(board[targetY][targetX].color); m < colors.size() ; m++)
                            {
                                board[targetY+1][targetX].colorsDomain.remove(colors.get(m));
                            }
                        }
                    }
                }
                if (targetX+1 != boardSize)
                {
                    if (board[targetY][targetX+1].color != null) {
                        if (colors.indexOf(board[targetY][targetX].color) > colors.indexOf(board[targetY][targetX + 1].color)) {
                            for (int m = 1; m <= board[targetY][targetX].number; m++) {
                                board[targetY][targetX + 1].numbersDomain.remove((Integer) m);
                            }
                        } else {
                            for (int m = board[targetY][targetX].number; m <= boardSize; m++) {
                                board[targetY][targetX + 1].numbersDomain.remove((Integer) m);
                            }
                        }
                    } else if(board[targetY][targetX+1].number != 0) {
                        if (board[targetY][targetX].number > board[targetY][targetX+1].number)
                        {
                            for (int m = 0; m <= colors.indexOf(board[targetY][targetX].color); m++)
                            {
                                board[targetY][targetX+1].colorsDomain.remove(colors.get(m));
                            }
                        } else {
                            for (int m = colors.indexOf(board[targetY][targetX].color); m < colors.size() ; m++)
                            {
                                board[targetY][targetX+1].colorsDomain.remove(colors.get(m));
                            }
                        }
                    }
                }
                if (targetY-1 != -1)
                {
                    if (board[targetY-1][targetX].color != null) {
                        if (colors.indexOf(board[targetY][targetX].color) > colors.indexOf(board[targetY - 1][targetX].color)) {
                            for (int m = 1; m <= board[targetY][targetX].number; m++) {
                                board[targetY - 1][targetX].numbersDomain.remove((Integer) m);
                            }
                        } else {
                            for (int m = board[targetY][targetX].number; m <= boardSize; m++) {
                                board[targetY - 1][targetX].numbersDomain.remove((Integer) m);
                            }
                        }
                    } else if(board[targetY-1][targetX].number != 0) {
                        if (board[targetY][targetX].number > board[targetY-1][targetX].number)
                        {
                            for (int m = 0; m <= colors.indexOf(board[targetY][targetX].color); m++)
                            {
                                board[targetY-1][targetX].colorsDomain.remove(colors.get(m));
                            }
                        } else {
                            for (int m = colors.indexOf(board[targetY][targetX].color); m < colors.size() ; m++)
                            {
                                board[targetY-1][targetX].colorsDomain.remove(colors.get(m));
                            }
                        }
                    }
                }
                if (targetX-1 != -1)
                {
                    if (board[targetY][targetX-1].color != null) {
                        if (colors.indexOf(board[targetY][targetX].color) > colors.indexOf(board[targetY][targetX - 1].color)) {
                            for (int m = 1; m <= board[targetY][targetX].number; m++) {
                                board[targetY][targetX - 1].numbersDomain.remove((Integer) m);
                            }
                        } else {
                            for (int m = board[targetY][targetX].number; m <= boardSize; m++) {
                                board[targetY][targetX - 1].numbersDomain.remove((Integer) m);
                            }
                        }
                    } else if(board[targetY][targetX-1].number != 0) {
                        if (board[targetY][targetX].number > board[targetY][targetX-1].number)
                        {
                            for (int m = 0; m <= colors.indexOf(board[targetY][targetX].color); m++)
                            {
                                board[targetY][targetX-1].colorsDomain.remove(colors.get(m));
                            }
                        } else {
                            for (int m = colors.indexOf(board[targetY][targetX].color); m < colors.size() ; m++)
                            {
                                board[targetY][targetX-1].colorsDomain.remove(colors.get(m));
                            }
                        }
                    }
                }
            }
        }
    }

    public static ArrayList<State> makeChildren(State state)
    {
        ArrayList<State> children = new ArrayList<>();
        for (int i = 0; i < boardSize; i++)
        {
            for (int j = 0; j < boardSize; j++)
            {
                if (state.board[i][j].color == null && state.board[i][j].colorsDomain.size() != 0)
                    for (String color : state.board[i][j].colorsDomain) {
                        Block[][] newBoard = copyBoard(state.board);
                        newBoard[i][j].color = color;
                        State newState = new State();
                        newState.parent = state;
                        newState.board = newBoard;
                        for (int v = 0; v < boardSize; v++)
                            for (int b = 0; b < boardSize; b++) forwardChecking(newState.board, v, b);
                        children.add(newState);
                        childrenCount++;
                    }
                if (state.board[i][j].number == 0 && state.board[i][j].numbersDomain.size() != 0)
                    for (Integer number : state.board[i][j].numbersDomain) {
                        Block[][] newBoard = copyBoard(state.board);
                        newBoard[i][j].number = number;
                        State newState = new State();
                        newState.parent = state;
                        newState.board = newBoard;
                        for (int v = 0; v < boardSize; v++)
                            for (int b = 0; b < boardSize; b++) forwardChecking(newState.board, v, b);
                        children.add(newState);
                        childrenCount++;
                    }
            }
        }
        children.sort(State::compareTo);
        return children;
    }

    public static boolean isTarget(State state)
    {
        return IntStream.range(0, boardSize).noneMatch(i -> IntStream.range(0, boardSize).anyMatch(j -> state.board[i][j].color == null || state.board[i][j].number == 0));
    }

    public static boolean isReachedToDeadLock(State state)
    {
        return IntStream.range(0, boardSize).anyMatch(i -> IntStream.range(0, boardSize).anyMatch(j -> (state.board[i][j].color == null && state.board[i][j].colorsDomain.size() == 0) || (state.board[i][j].number == 0 && state.board[i][j].numbersDomain.size() == 0)));
    }

    private static Block[][] copyBoard(Block[][] board)
    {
        Block[][] newBoard = new Block[boardSize][boardSize];
        for (int k = 0; k < boardSize; k++) {
            for (int m = 0; m < boardSize; m++) {
                Block newBlock = new Block();
                newBlock.number = board[k][m].number;
                newBlock.color = board[k][m].color;
                newBlock.numbersDomain = new ArrayList<>(board[k][m].numbersDomain);
                newBlock.colorsDomain = new ArrayList<>(board[k][m].colorsDomain);
                newBlock.board = board;
                newBoard[k][m] = newBlock;
            }
        }
        return newBoard;
    }

    public static State backwardSearch(State state)
    {
        for (State child : makeChildren(state))
        {
            if (isReachedToDeadLock(child))
            {
                return null;
            }
            if (isTarget(child))
            {
                return child;
            }
            State result = backwardSearch(child);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    public static void printState(State state)
    {
        System.out.println("::::::::::::::::::");
        for (int i = 0; i < boardSize; i++)
        {
            for (int j = 0; j < boardSize; j++)
            {
                String color = state.board[i][j].color == null ? "#" : state.board[i][j].color;
                System.out.print(state.board[i][j].number + color + "|");
            }
            System.out.println();
        }
        System.out.println("::::::::::::::::::");
    }

    public static Block getChangedBlockWithParent(State state)
    {
        for (int i = 0; i < boardSize; i++)
        {
            for (int j = 0; j < boardSize; j++)
            {
                if (state.board[i][j].number != state.parent.board[i][j].number || state.board[i][j].color != state.parent.board[i][j].color)
                {
                    return state.board[i][j];
                }
            }
        }
        return null;
    }
}
