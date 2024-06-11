import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;

public class GameFrame extends JFrame {
    final int ROWS = 11,
              COLS = 8;


    int flagsplaced = 0;
    private final JLabel LABEL;

    private final JPanel GRID;
    private final Tile[][] TILE_TABLE;

    private final int TOTAL_MINES = 10;
    private ArrayList<Point> riggedPoints;
    int normalTiles = ROWS * COLS -TOTAL_MINES;

    public GameFrame(){
        super("MineSweeper");
        setLayout(new BorderLayout());

        LABEL = new JLabel("Flags: 10");
        LABEL.setHorizontalAlignment(SwingConstants.CENTER);
        LABEL.setFont(new Font("Arial Unicode MS", Font.BOLD, 25));

        GRID = new JPanel(new GridLayout(COLS,ROWS));
        TILE_TABLE = new Tile[COLS][ROWS];
        riggedPoints = new ArrayList<>();
        setRiggedPoints();
        initializeTiles();
        notifyNeighbors();

        add(LABEL, BorderLayout.NORTH);
        add(GRID);



        setSize(800,800);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);



    }

    class TileHandler implements MouseListener{
        @Override
        public void mouseClicked(MouseEvent event) {
            Tile tile = (Tile) event.getSource();
            if(SwingUtilities.isRightMouseButton(event)) {
                if (tile.getText().isEmpty() && flagsplaced != TOTAL_MINES) {
                    LABEL.setText("Flags: " + (TOTAL_MINES - ++flagsplaced));
                    tile.setForeground(Color.BLACK);
                    tile.setText(Tile.FLAG);
                }
                else if (tile.getText().equals(Tile.FLAG)){
                    LABEL.setText("Flags: " + (TOTAL_MINES - --flagsplaced));
                    tile.setText("");
                }
            }
            else
                revealTile(tile);

        }

        public void mousePressed(MouseEvent e){
            mouseClicked(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseClicked(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }



    private void setRiggedPoints(){
        Random rand = new Random();
        for(int i = 0; i < TOTAL_MINES; i++){
            Point point = new Point();
            do{
                int row = rand.nextInt(ROWS),
                    col = rand.nextInt(COLS);
                point.setLocation(col,row);
            }
            while(riggedPoints.contains(point.getLocation()));
            riggedPoints.add( point );
            //temp
            System.out.println(point.getLocation());
            //
        }
    }

    private void initializeTiles(){
        for(int col = 0; col < COLS; col++)
            for(int row = 0; row < ROWS; row++){
                Point tilePoint = new Point(col,row);
                boolean hasMine = riggedPoints.contains(tilePoint.getLocation());
                TILE_TABLE[col][row] = new Tile(col,row,hasMine);
                TILE_TABLE[col][row].setFocusable(false);
                TILE_TABLE[col][row].setFont(new Font("Arial Unicode MS", Font.PLAIN, 35));
                TILE_TABLE[col][row].addMouseListener(new TileHandler());
                TILE_TABLE[col][row].setBackground(Color.LIGHT_GRAY);
                GRID.add(TILE_TABLE[col][row]);
            }
    }



    private void notifyNeighbors(){
        for(Point point : riggedPoints){
            int col = point.x;
            int row = point.y;

            int colStart = (col == 0) ? col : col - 1,
                colEnd = (col == COLS - 1) ? col : col + 1;

            int rowStart = (row == 0) ? row : row - 1,
                rowEnd = (row == ROWS - 1) ? row : row + 1;

            for(int i = colStart; i <= colEnd; i++)
                for(int j = rowStart; j <= rowEnd; j++)
                    setNeighborContent(TILE_TABLE[i][j]);
        }
    }

    private void setNeighborContent(Tile tile){
            if(!tile.hasMine())
                if(tile.getHiddenContent().equals("."))
                    tile.setHiddenContent(Integer.toString(1));
                else
                    tile.setHiddenContent(Integer.toString( Integer.parseInt(tile.getHiddenContent()) + 1   )   );
        System.out.println(tile.getHiddenContent());
    }

    private void revealTile(Tile tile){
        if(tile.getText().isEmpty()){
            if(tile.getHiddenContent().equals(".")){
                int col = tile.COORDINATE.x;
                int row = tile.COORDINATE.y;


                int colStart = col - 1,
                        colEnd = col + 1;

                int rowStart = row - 1,
                        rowEnd = row + 1;


                tile.setBackground(Color.GRAY);
                tile.revealHiddenContent();
                normalTiles--;
                System.out.printf("(%s,%s)\n", tile.COORDINATE.y, tile.COORDINATE.x);

                if(colStart >= 0 && TILE_TABLE[colStart][row].getText().isEmpty())
                    revealTile(TILE_TABLE[colStart][row]);
                if(colEnd <= COLS - 1 && TILE_TABLE[colEnd][row].getText().isEmpty())
                    revealTile(TILE_TABLE[colEnd][row]);
                if(rowStart >= 0 && TILE_TABLE[col][rowStart].getText().isEmpty())
                    revealTile(TILE_TABLE[col][rowStart]);
                if(rowEnd <= ROWS - 1 && TILE_TABLE[col][rowEnd].getText().isEmpty())
                    revealTile(TILE_TABLE[col][rowEnd]);
//                if(colStart >= 0 && rowStart >= 0 && TILE_TABLE[colStart][rowStart].getText().isEmpty())
//                    revealTile(TILE_TABLE[colStart][rowStart]);
//                if(colEnd <=  COLS - 1 && rowStart >= 0 && TILE_TABLE[colEnd][rowStart].getText().isEmpty())
//                    revealTile(TILE_TABLE[colEnd][rowStart]);
//                if(colStart >=  0 && rowEnd <= ROWS - 1 && TILE_TABLE[colEnd][rowStart].getText().isEmpty())
//                    revealTile(TILE_TABLE[colStart][rowEnd]);
//                if(colEnd <=  COLS - 1 && rowEnd <= ROWS - 1 && TILE_TABLE[colEnd][rowStart].getText().isEmpty())
//                    revealTile(TILE_TABLE[colEnd][rowEnd]);

            }
            else if(tile.getHiddenContent().equals(Tile.MINE)){
                tile.setBackground(Color.RED);
                for(Point point : riggedPoints)
                    TILE_TABLE[point.x][point.y].revealHiddenContent();
                JOptionPane.showMessageDialog(LABEL,"YOU LOST!","",JOptionPane.ERROR_MESSAGE);
                reset();

            }
            else{
                tile.setBackground(Color.GRAY);
                tile.revealHiddenContent();
                normalTiles--;
            }
        }

        if(normalTiles == 0){
            JOptionPane.showMessageDialog(LABEL,"YOU WON!","",JOptionPane.INFORMATION_MESSAGE);
            reset();
        }

    }

    public void reset(){
        for(Tile[] row : TILE_TABLE)
            for(Tile cell : row){
                cell.setText("");
                cell.setBackground(Color.LIGHT_GRAY);
                cell.setHiddenContent(".");
            }

        riggedPoints = new ArrayList<>();
        setRiggedPoints();

        for(Point point : riggedPoints){
            int col = point.getLocation().x;
            int row = point.getLocation().y;

            TILE_TABLE[col][row].setHiddenContent(Tile.MINE);
        }

        notifyNeighbors();
        normalTiles = ROWS * COLS - TOTAL_MINES;
        flagsplaced = 0;
        LABEL.setText("Flags: " + (TOTAL_MINES - flagsplaced));
    }
}

class Tile extends JButton{
    final Point COORDINATE;
    static final String FLAG = "🚩";
    static final String MINE = "\uD83D\uDCA3";
    private String hiddenContent;

    static final Map<String,Color> colorMap = new HashMap<>();


    public Tile(Point coordinate, boolean mineExists){
        super();
        COORDINATE = coordinate;
        hiddenContent = (mineExists) ? MINE : ".";

        colorMap.put(".", Color.GRAY);
        colorMap.put("1", Color.BLUE);
        colorMap.put("2", Color.GREEN);
        colorMap.put("3", Color.RED);
        colorMap.put("4", Color.ORANGE);
        colorMap.put("5", Color.MAGENTA);
        colorMap.put("6", Color.cyan);
        colorMap.put("7", Color.white);
        colorMap.put("8", Color.BLACK);
        colorMap.put(FLAG,Color.BLACK);

    }

    public Tile(int x, int y, boolean mineExists){
        this(new Point(x,y), mineExists);
    }

    public boolean hasMine(){
        return hiddenContent.equals(MINE);
    }

    public String getHiddenContent(){
        return hiddenContent;
    }

    public void setHiddenContent(String str){
        hiddenContent = str;
    }

    public void revealHiddenContent(){
        setForeground(colorMap.get(hiddenContent));
        setText(hiddenContent);
    }


}