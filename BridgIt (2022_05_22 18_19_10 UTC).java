import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.*;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

//represents a position on a Cartesian Graph
class Posn {
  int x;
  int y;

  Posn(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  /*
   * Template:
   *  Fields:
   *    this.x ... int
   *    this.y ... int
   *  Methods:
   *    this.samePosn(Posn) ... boolean
   *    this.samePosn(Posn, double, double) ... boolean
   *    this.changeLoc(int,int) ... void
   *  Methods on Fields:
   *    ...
   * */

  // does this posn equal to that posn
  public boolean samePosn(Posn that) {
    return this.x == that.x && this.y == that.y;
  }

  // does this posn equal to that posn within a range
  public boolean samePosn(Posn that, double dx, double dy) {
    return Math.abs(this.x - that.x) <= dx && Math.abs(this.y - that.y) <= dy;
  }

  // EFFECT: changes the x and y position by dx and dy resp.
  public void changeLoc(int dx, int dy) {
    this.x += dx;
    this.y += dy;
  }
}

//represent a cell with color
class Cell {
  Color color;
  Posn position;
  Cell left;
  Cell right;
  Cell above;
  Cell below;

  // constructor with only color
  Cell(Color color, Posn position) {
    this.color = color;
    this.position = position;
  }

  // constructor with neighbors
  Cell(Color color, Posn position, Cell left, Cell right, Cell above, Cell below) {
    this(color, position);
    if ((this.left == null && this.right == null) || (this.above == null && this.below == null)) {
      throw new IllegalArgumentException("Given Cell is null!");
    }
    else {
      this.left = left;
      this.right = right;
      this.above = above;
      this.below = below;
    }
  }
  
  /* Template:
   *  Fields:
   *    this.color ... Color
   *    this.position ... Posn
   *    this.left ... Cell
   *    this.right ... Cell
   *    this.above  ... Cell
   *    this.below ... Cell
   *  Methods:
   *    this.draw() ... WorldImage
   *    this.setNeighbor(Cell, String) ... void
   *    this.isBlank() ... boolean
   *    this.changeColor(Color) ... void
   *    this.isEdge() ... boolean
   *    this.sameColor(Cell) ... boolean
   *    this.edgeType() ... String
   *    this.hasPathTo(Cell) ... boolean
   *  Methods on Fields:
   *    this.position.sameLoc(Posn) ... boolean
   *    this.position.samePosn(Posn, double, double) ... boolean
   *    this.position.changeLoc(int, int) ... void
   *    this.left.draw() ... WorldImage
   *    this.left.setNeighbor(Cell, String) ... void
   *    this.left.isBlank() ... boolean
   *    this.left.changeColor(Color) ... void
   *    this.left.isEdge() ... boolean
   *    this.left.sameColor(Cell) ... boolean
   *    this.left.edgeType() ... String
   *    this.left.hasPathTo(Cell) ... boolean
   *    this.right.draw() ... WorldImage
   *    this.right.setNeighbor(Cell, String) ... void
   *    this.right.isBlank() ... boolean
   *    this.right.changeColor(Color) ... void
   *    this.right.isEdge() ... boolean
   *    this.right.sameColor(Cell) ... boolean
   *    this.right.edgeType() ... String
   *    this.right.hasPathTo(Cell) ... boolean
   *    this.above.draw() ... WorldImage
   *    this.above.setNeighbor(Cell, String) ... void
   *    this.above.isBlank() ... boolean
   *    this.above.changeColor(Color) ... void
   *    this.above.isEdge() ... boolean
   *    this.above.sameColor(Cell) ... boolean
   *    this.above.edgeType() ... String
   *    this.above.hasPathTo(Cell) ... boolean
   *    this.below.draw() ... WorldImage
   *    this.below.setNeighbor(Cell, String) ... void
   *    this.below.isBlank() ... boolean
   *    this.below.changeColor(Color) ... void
   *    this.below.isEdge() ... boolean
   *    this.below.sameColor(Cell) ... boolean
   *    this.below.edgeType() ... String
   *    this.below.hasPathTo(Cell) ... boolean
   * */

  // draw the cell
  public WorldImage draw() {
    int length = 100;
    return new RectangleImage(length, length, OutlineMode.SOLID, color);
  }

  // EFFECT: Sets either the left, right, above or below Cells
  public void setNeighbor(Cell cell, String direction) {
    if (direction.startsWith("l")) {
      this.left = cell;
      cell.right = this;
    }
    else if (direction.startsWith("r")) {
      this.right = cell;
      cell.left = this;
    }
    else if (direction.startsWith("a")) {
      this.above = cell;
      cell.below = this;
    }
    else if (direction.startsWith("b")) {
      this.below = cell;
      cell.above = this;
    }
    else {
      throw new RuntimeException("Invalid Directional Cell");
    }
  }

  // returns true if this cell is a white cell
  public boolean isBlank() {
    return this.color.equals(Color.WHITE);
  }

  // EFFECT: changes the color of this cell
  public void changeColor(Color c) {
    this.color = c;
  }

  // returns true if this cell is an edge
  public boolean isEdge() {
    return this.above == null || this.below == null || this.left == null || this.right == null;
  }

  // returns true if this cell has the same color as that cell
  public boolean sameColor(Cell that) {
    return this.color.equals(that.color);
  }

  // returns edge type
  public String edgeType() {
    if (this.isEdge() && !this.isBlank()) {
      if (this.above == null && this.color.equals(Color.MAGENTA)) {
        return "a";
      }

      else if (this.below == null && this.color.equals(Color.MAGENTA)) {
        return "b";
      }

      else if (this.left == null && this.color.equals(Color.PINK)) {
        return "l";
      }

      else if (this.right == null && this.color.equals(Color.PINK)) {
        return "r";
      }
      else {
        return "na";
      }
    }
    else {
      return "na";
    }
  }

  // returns true if there's a path to the given cell
  public boolean hasPathTo(Cell that) {
    ArrayList<Cell> queue = new ArrayList<Cell>();
    ArrayList<Cell> visited = new ArrayList<Cell>();

    // initializes queue for the cell that called this method
    queue.add(this);

    while (!queue.isEmpty()) {
      Cell next = queue.remove(0); // removes the next cell in queue

      // found a path to the cell
      if (next.equals(that)) {
        return true;
      }

      else if (visited.contains(next)) {
        // do nothing
      }

      else {
        // adds valid neighbors to the queue
        if (next.right != null && next.sameColor(next.right) && !visited.contains(next.right)) {
          queue.add(next.right);
        }
        if (next.left != null && next.sameColor(next.left) && !visited.contains(next.left)) {
          queue.add(next.left);
        }
        if (next.above != null && next.sameColor(next.above) && !visited.contains(next.above)) {
          queue.add(next.above);
        }
        if (next.below != null && next.sameColor(next.below) && !visited.contains(next.below)) {
          queue.add(next.below);
        }

        visited.add(next);
      }
    }
    return false;
  }
}

//represents the game class
class BridgIt extends World {
  int gameSize;
  ArrayList<Cell> cells;
  boolean pinkTurn;
  boolean pinkWin = false;
  boolean magWin = false;
  ArrayList<Cell> leftEdge;
  ArrayList<Cell> rightEdge;
  ArrayList<Cell> topEdge;
  ArrayList<Cell> bottomEdge;

  // Constructors with only size
  BridgIt(int gameSize) {
    if (gameSize <= 3 || gameSize % 2 == 0) {
      throw new IllegalArgumentException("Invalid Game Size!");
    }
    else {
      this.gameSize = gameSize;
    }
    this.cells = this.createBoard();
    this.pinkTurn = true;
    this.connectCells();

    this.leftEdge = this.allEdges("l");
    this.rightEdge = this.allEdges("r");
    this.topEdge = this.allEdges("a");
    this.bottomEdge = this.allEdges("b");
  }

  // Constructor with all parameters
  BridgIt(int gameSize, ArrayList<Cell> cells) {
    this.gameSize = gameSize;
    this.cells = cells;
    this.pinkTurn = true;
    this.leftEdge = this.allEdges("l");
    this.rightEdge = this.allEdges("r");
    this.topEdge = this.allEdges("a");
    this.bottomEdge = this.allEdges("b");
  }

  // Constructors with no parameters
  BridgIt() {
    this(11);
    this.connectCells();
    this.leftEdge = this.allEdges("l");
    this.rightEdge = this.allEdges("r");
    this.topEdge = this.allEdges("a");
    this.bottomEdge = this.allEdges("b");
  }
  
  /*
   * Template: 
   *  Fields:
   *    this.gameSize ... int
   *    this.cells ... ArrayList<Cell>
   *    this.pinkTurn ... boolean
   *    this.pinkWin ... boolean
   *    this.magWin ... boolean
   *    this.leftEdge ... ArrayList<Cell>
   *    this.rightEdge ... ArrayList<Cell>
   *    this.topEdge ... ArrayList<Cell>
   *    this.bottomEdge ... ArrayList<Cell>
   *    
   *  Methods:
   *    this.createBoard() ... ArrayList<Cell>
   *    this.connectCells() ... void
   *    this.drawCells() ... WorldScene
   *    this.displayTurn(WorldScene) ... WorldScene
   *    this.makeScene() ... WorldScene
   *    this.onMouseClicked(Posn) ... void
   *    this.cellSelect(Posn) ... void
   *    this.allEdges(String) ... ArrayList<Cell>
   *    this.victory() ... void
   *    this.onTick() ... void
   *    this.onKeyEvent(String) ... void
   *    this.endImage(boolean) ... WorldScene
   *    this.worldEnds() ... WorldEnd
   *  Methods on Fields:
   *    this.cells.get(int) ... Cell
   *    this.cells.size() ... int
   *    this.leftEdge.get(int) ... Cell
   *    this.leftEdge.size() ... int
   *    this.rightEdge.get(int) ... Cell
   *    this.rightEdge.size() ... int
   *    this.topEdge.get(int) ... Cell
   *    this.topEdge.size() ... int
   *    this.bottomEdge.get(int) ... Cell
   *    this.bottomEdge.size() ... int
   *    ... ArrayList API ...
   * */

  // make unconnected array list of cells
  public ArrayList<Cell> createBoard() {
    ArrayList<Cell> board = new ArrayList<Cell>();
    for (int i = 0; i < this.gameSize; i++) {
      board.addAll(new ArrayUtils().buildList(this.gameSize, new MakeCells(i)));
    }

    return board;
  }

  // EFFECT: connects neighbors for each cell
  public void connectCells() {
    int lastIndex = (this.gameSize - 1);
    for (int i = 0; i < this.cells.size(); i++) {

      // left case
      if (this.cells.get(i).position.x > 50) {
        this.cells.get(i).setNeighbor(this.cells.get(i - 1), "l");
      }

      // right case
      if (this.cells.get(i).position.x < (lastIndex * 100 + 50)) {
        this.cells.get(i).setNeighbor(this.cells.get(i + 1), "r");
      }

      // above case
      if (this.cells.get(i).position.y > 50) {
        this.cells.get(i).setNeighbor(this.cells.get(i - this.gameSize), "a");
      }

      // below case
      if (this.cells.get(i).position.y < (lastIndex * 100 + 50)) {
        this.cells.get(i).setNeighbor(this.cells.get(i + this.gameSize), "b");
      }
    }
  }

  // draw the cells
  public WorldScene drawCells() {
    WorldScene scene = this.getEmptyScene();

    for (Cell cell : this.cells) {
      scene.placeImageXY(cell.draw(), cell.position.x, cell.position.y);
    }

    return scene;
  }

  // draw the turn
  public WorldScene displayTurn(WorldScene prev) {
    WorldScene nextScene = prev;
    WorldImage turnImg = new TextImage("Turn: ", 40, FontStyle.BOLD, Color.BLACK);
    int imgX = (this.gameSize + 1) * 100;
    int imgY = (this.gameSize / 2) * 100;

    if (this.pinkTurn) {
      nextScene.placeImageXY(new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
          new TextImage("PINK", 40, FontStyle.BOLD, Color.PINK), 0, -40, turnImg), imgX, imgY);
    }
    else {
      nextScene.placeImageXY(
          new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
              new TextImage("MAGENTA", 40, FontStyle.BOLD, Color.MAGENTA), 0, -40, turnImg),
          imgX, imgY);
    }

    return nextScene;
  }

  // draw the scene
  public WorldScene makeScene() {
    return this.displayTurn(this.drawCells());
  }

  // EFFECT: changes color of the cell when selected and valid
  public void onMouseClicked(javalib.worldimages.Posn pos) {
    Posn mouse = new Posn(pos.x, pos.y);
    this.cellSelect(mouse);
  }

  // EFFECT: changes color of the cell and switches the turn
  public void cellSelect(Posn pos) {
    for (Cell c : this.cells) {
      if (c.position.samePosn(pos, 50, 50) && c.isBlank() && !c.isEdge()) {
        if (this.pinkTurn) {
          this.cells.get(this.cells.indexOf(c)).changeColor(Color.PINK);
        }
        else {
          this.cells.get(this.cells.indexOf(c)).changeColor(Color.MAGENTA);
        }
        this.pinkTurn = (!this.pinkTurn);
      }
    }
  }

  // returns all edges in the board
  public ArrayList<Cell> allEdges(String type) {
    ArrayList<Cell> edges = new ArrayList<Cell>();

    // System.out.println("Type: \"" + type + "\": "); // testing purposes
    for (Cell c : this.cells) {
      if (c.edgeType().equals(type)) {

        // System.out.println("Color: " + c.color); // testing purposes
        edges.add(c);
      }
    }

    return edges;
  }

  // EFFECT: changes if one of the colors win
  public void victory() {
    boolean pink = false;
    boolean magenta = false;
    int arrSize1 = this.leftEdge.size();
    int arrSize2 = this.rightEdge.size();

    for (int i = 0; i < arrSize1; i++) {
      for (int j = 0; j < arrSize2; j++) {
        pink = pink || (this.leftEdge.get(i).hasPathTo(this.rightEdge.get(j)));
        magenta = magenta || (this.topEdge.get(i).hasPathTo(this.bottomEdge.get(j)));
      }
    }

    if (pink) {
      this.pinkWin = true;
    }
    else if (magenta) {
      this.magWin = true;
    }
    else {
      this.pinkWin = false;
      this.magWin = false;
    }
  }

  // EFFECT: checks if any of the colors win
  public void onTick() {
    this.victory();
  }
  
  // EFFECT: resets all fields and creates a new board
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.cells = this.createBoard();
      this.pinkTurn = true;
      this.connectCells();
      this.pinkWin = false;
      this.magWin = false;
    }
  }

  // returns the end world scene
  public WorldScene endImage(boolean pink) {
    WorldScene scene = this.getEmptyScene();

    if (pink) {
      WorldImage pinkImg = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
          new TextImage("Congratulations!", 30, FontStyle.BOLD, Color.MAGENTA), 0, 10,
          new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
              new TextImage("PINK WINS!", 20, FontStyle.BOLD, Color.PINK), 0, -30,
              new StarImage(200, OutlineMode.SOLID, Color.YELLOW)));

      scene.placeImageXY(pinkImg, scene.width / 2, scene.height / 2);
    }
    else {
      WorldImage magImg = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
          new TextImage("Congraulations!", 30, FontStyle.BOLD, Color.MAGENTA), 0, 10,
          new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
              new TextImage("MAGENTA WINS!", 20, FontStyle.BOLD, Color.MAGENTA), 0, -30,
              new StarImage(200, OutlineMode.SOLID, Color.YELLOW)));

      scene.placeImageXY(magImg, scene.width / 2, scene.height / 2);

    }
    return scene;
  }

  // ends world when there's a winner selected
  public WorldEnd worldEnds() {
    if (this.pinkWin) {
      return new WorldEnd(true, this.endImage(true));
    }
    else if (this.magWin) {
      return new WorldEnd(true, this.endImage(false));
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }
}

//array utilities
class ArrayUtils {

  /*
   * Template: 
   *  Field: 
   *    ...
   *  Method: 
   *    this.buildList(int, Function<Interger, U>) ... ArrayList<U>
   *  Method on Fields:
   *    ...
   */
  
  // Build List method
  <U> ArrayList<U> buildList(int n, Function<Integer, U> func) {
    ArrayList<U> answer = new ArrayList<U>();
    for (int i = 0; i < n; i++) {
      answer.add(func.apply(i));
    }

    return answer;
  }
}

// function that creates the cells
class MakeCells implements Function<Integer, Cell> {
  int row;

  MakeCells(int row) {
    this.row = row;
  }
  
  /*
   * Template: 
   *  Field: 
   *    this.row ... int
   *  Method: 
   *    this.apply(int) ... Cell
   *  Method on Fields:
   *    ...
   */

  // Returns the right cell for the right number
  public Cell apply(Integer t) {
    int size = 100;
    Posn newLoc = new Posn((t * size) + (size / 2), (this.row * size) + (size / 2));

    if (row % 2 == 0) {
      if (t % 2 == 0) {
        return new Cell(Color.WHITE, newLoc);
      }
      else {
        return new Cell(Color.MAGENTA, newLoc);
      }
    }

    else {
      if (t % 2 == 0) {
        return new Cell(Color.PINK, newLoc);
      }
      else {
        return new Cell(Color.WHITE, newLoc);
      }
    }
  }
}

// a class that represents examples of BridgIt
class ExamplesBridgIt {
  BridgIt game1;
  BridgIt game2;

  WorldImage magenta;
  WorldImage pink;
  WorldImage white;

  Cell cell0;
  Cell cell1;
  Cell cell5;
  Cell cell6;
  Cell cell7;
  Cell cell8;
  Cell cell9;

  // initializes data
  void initData() {
    int length = 100;
    this.game1 = new BridgIt(5);
    this.game2 = new BridgIt(5, new ArrayList<Cell>());

    this.magenta = new RectangleImage(length, length, OutlineMode.SOLID, Color.MAGENTA);
    this.pink = new RectangleImage(length, length, OutlineMode.SOLID, Color.PINK);
    this.white = new RectangleImage(length, length, OutlineMode.SOLID, Color.WHITE);

    this.cell0 = new Cell(Color.WHITE, new Posn(50, 50));
    this.cell1 = new Cell(Color.MAGENTA, new Posn(150, 50));
    this.cell5 = new Cell(Color.PINK, new Posn(50, 150));
    this.cell6 = new Cell(Color.PINK, new Posn(150, 150));
    this.cell7 = new Cell(Color.PINK, new Posn(250, 150));
    this.cell8 = new Cell(Color.PINK, new Posn(350, 150));
    this.cell9 = new Cell(Color.PINK, new Posn(450, 150));

  }

  // test creating the board
  void testCreateBoard(Tester t) {
    this.initData();

    t.checkExpect(this.game2.cells, new ArrayList<Cell>());

    this.game2 = new BridgIt(5, this.game1.createBoard());

    t.checkExpect(this.game2.cells.size(), 25);

  }

  // test neighbors
  void testNeighbors(Tester t) {
    this.initData();

    this.game2.cells = this.game2.createBoard();
    ArrayList<Cell> board1 = this.game2.cells;
    Cell initCell1 = board1.get(1);
    Cell initCell2 = board1.get(6);

    t.checkExpect(initCell1.left, null);
    t.checkExpect(initCell1.above, null);
    t.checkExpect(initCell1.below, null);
    t.checkExpect(initCell1.right, null);

    t.checkExpect(initCell2.left, null);
    t.checkExpect(initCell2.above, null);
    t.checkExpect(initCell2.below, null);
    t.checkExpect(initCell2.right, null);

    this.game2.connectCells();

    // updated cell list and cells
    ArrayList<Cell> board2 = this.game2.cells;
    Cell finalCell1 = board2.get(1);
    Cell finalCell2 = board2.get(6);

    t.checkExpect(finalCell1.left, board2.get(0));
    t.checkExpect(finalCell1.above, null);
    t.checkExpect(finalCell1.below, finalCell2);
    t.checkExpect(finalCell1.right, board2.get(2));

    t.checkExpect(finalCell2.left, board2.get(5));
    t.checkExpect(finalCell2.above, finalCell1);
    t.checkExpect(finalCell2.below, board2.get(11));
    t.checkExpect(finalCell2.right, board2.get(7));

  }

  // test draw function
  void testDraw(Tester t) {
    this.initData();

    t.checkExpect(this.cell0.draw(), this.white);
    t.checkExpect(this.cell1.draw(), this.magenta);
    t.checkExpect(this.cell5.draw(), this.pink);
  }

  // test make scene
  void testMakeScene(Tester t) {
    this.initData();
    BridgIt w = new BridgIt(5);
    WorldScene testScene = w.getEmptyScene();
    for (Cell cell : w.cells) {
      testScene.placeImageXY(cell.draw(), cell.position.x, cell.position.y);
    }

    t.checkExpect(w.makeScene(), testScene);

  }

  // test make cells
  void testMakeCells(Tester t) {
    this.initData();

    MakeCells mc = new MakeCells(0);

    t.checkExpect(mc.apply(0), this.cell0);
    t.checkExpect(mc.apply(1), this.cell1);

    mc = new MakeCells(1);
    t.checkExpect(mc.apply(0), this.cell5);
  }

  // test hasPathTo
  void testHasPathTo(Tester t) {
    this.initData();
    t.checkExpect(this.cell0.hasPathTo(this.cell1), false);

    this.cell5.right = this.cell6;
    this.cell6.right = this.cell7;
    this.cell7.right = this.cell8;
    this.cell8.right = this.cell9;

    this.cell6.left = this.cell5;
    this.cell7.left = this.cell6;
    this.cell8.left = this.cell7;
    this.cell9.left = this.cell8;

    t.checkExpect(this.cell5.hasPathTo(this.cell6), true);
    t.checkExpect(this.cell5.hasPathTo(this.cell7), true);
    t.checkExpect(this.cell5.hasPathTo(this.cell8), true);
    t.checkExpect(this.cell5.hasPathTo(this.cell9), true);
    t.checkExpect(this.cell5.hasPathTo(this.cell1), false);

  }

  // test victory
  void testVictory(Tester t) {
    this.initData();

    ArrayList<Cell> testCells = new ArrayList<Cell>(
        Arrays.asList(this.cell5, this.cell6, this.cell7, this.cell8, this.cell9));
    BridgIt test1 = new BridgIt(5, testCells);

    t.checkExpect(test1.pinkWin, false);

    // connect cells
    this.cell5.right = this.cell6;
    this.cell6.right = this.cell7;
    this.cell7.right = this.cell8;
    this.cell8.right = this.cell9;

    this.cell6.left = this.cell5;
    this.cell7.left = this.cell6;
    this.cell8.left = this.cell7;
    this.cell9.left = this.cell8;

    test1.leftEdge = new ArrayList<Cell>();
    test1.leftEdge.add(this.cell5);
    test1.rightEdge = new ArrayList<Cell>();
    test1.rightEdge.add(this.cell9);
    test1.topEdge = new ArrayList<Cell>();
    test1.topEdge.add(this.cell6);
    test1.bottomEdge = new ArrayList<Cell>();
    test1.bottomEdge.add(this.cell0);

    test1.victory();
    t.checkExpect(test1.pinkWin, true);

    this.game1.cellSelect(this.game1.cells.get(6).position);
    this.game1.pinkTurn = true;
    this.game1.cellSelect(this.game1.cells.get(8).position);

    t.checkExpect(this.game1.cells.get(6).color, Color.PINK);
    t.checkExpect(this.game1.cells.get(8).color, Color.PINK);

    this.game1.victory();
    t.checkExpect(this.game1.pinkWin, true);

    this.initData();

    this.game1.pinkTurn = false;
    this.game1.cellSelect(this.game1.cells.get(6).position);
    this.game1.pinkTurn = false;
    this.game1.cellSelect(this.game1.cells.get(16).position);

    t.checkExpect(this.game1.cells.get(6).color, Color.MAGENTA);
    t.checkExpect(this.game1.cells.get(16).color, Color.MAGENTA);

    this.game1.victory();
    t.checkExpect(this.game1.magWin, true);

  }

  // test edgeType
  void testEdgeType(Tester t) {
    this.initData();

    this.cell5.right = this.cell6;
    this.cell6.right = this.cell7;
    this.cell7.right = this.cell8;
    this.cell8.right = this.cell9;

    this.cell6.left = this.cell5;
    this.cell7.left = this.cell6;
    this.cell8.left = this.cell7;
    this.cell9.left = this.cell8;

    t.checkExpect(this.cell5.left, null);
    t.checkExpect(this.cell5.edgeType(), "l");

    t.checkExpect(this.cell9.edgeType(), "r");

    t.checkExpect(this.game1.cells.get(1).edgeType(), "a");
    t.checkExpect(this.game1.cells.get(21).edgeType(), "b");

  }

  // test all edges
  void testAllEdges(Tester t) {
    BridgIt test1 = new BridgIt(5);

    ArrayList<Cell> testLeft = new ArrayList<Cell>(
        Arrays.asList(test1.cells.get(5), test1.cells.get(15)));

    t.checkExpect(test1.allEdges("l"), testLeft);

    ArrayList<Cell> testRight = new ArrayList<Cell>(
        Arrays.asList(test1.cells.get(9), test1.cells.get(19)));

    t.checkExpect(test1.allEdges("r"), testRight);

    ArrayList<Cell> testAbove = new ArrayList<Cell>(
        Arrays.asList(test1.cells.get(1), test1.cells.get(3)));

    t.checkExpect(test1.allEdges("a"), testAbove);

    ArrayList<Cell> testBottom = new ArrayList<Cell>(
        Arrays.asList(test1.cells.get(21), test1.cells.get(23)));

    t.checkExpect(test1.allEdges("b"), testBottom);

  }

  // test cellSelect
  void testCellSelect(Tester t) {
    this.initData();

    t.checkExpect(this.game1.pinkTurn, true);
    t.checkExpect(this.game1.cells.get(6).position, new Posn(150, 150));
    t.checkExpect(this.game1.cells.get(8).position, new Posn(350, 150));
    t.checkExpect(this.game1.cells.get(6).isBlank(), true);
    t.checkExpect(this.game1.cells.get(8).isBlank(), true);
    t.checkExpect(this.game1.cells.get(6).color, Color.WHITE);
    t.checkExpect(this.game1.cells.get(8).color, Color.WHITE);
    t.checkExpect(this.game1.cells.get(6).isEdge(), false);
    t.checkExpect(this.game1.cells.get(8).isEdge(), false);

    this.game1.cellSelect(new Posn(150, 150));

    t.checkExpect(this.game1.pinkTurn, false);
    t.checkExpect(this.game1.cells.get(6).color, Color.PINK);

    this.game1.cellSelect(new Posn(350, 150));

    t.checkExpect(this.game1.pinkTurn, true);
    t.checkExpect(this.game1.cells.get(8).color, Color.MAGENTA);

  }

  // test isEdge
  void testIsEdge(Tester t) {
    this.initData();
    t.checkExpect(this.game1.cells.get(6).isEdge(), false);
    t.checkExpect(this.game1.cells.get(0).isEdge(), true);
    t.checkExpect(this.game1.cells.get(7).isEdge(), false);
  }

  // test isBlank
  void testIsBlank(Tester t) {
    this.initData();

    t.checkExpect(this.game1.cells.get(0).isBlank(), true);
    t.checkExpect(this.game1.cells.get(7).isBlank(), false);

    this.game1.cells.get(0).color = Color.black;

    t.checkExpect(this.game1.cells.get(0).isBlank(), false);
  }

  // test endImage
  void testEndImage(Tester t) {
    this.initData();

    WorldScene test1 = this.game1.getEmptyScene();
    WorldImage pinkImg = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
        new TextImage("Congratulations!", 30, FontStyle.BOLD, Color.MAGENTA), 0, 10,
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage("PINK WINS!", 20, FontStyle.BOLD, Color.PINK), 0, -30,
            new StarImage(200, OutlineMode.SOLID, Color.YELLOW)));
    test1.placeImageXY(pinkImg, 550, 450);

    t.checkExpect(this.game1.endImage(true), test1);

    WorldScene test2 = this.game1.getEmptyScene();
    WorldImage magImg = new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
        new TextImage("Congraulations!", 30, FontStyle.BOLD, Color.MAGENTA), 0, 10,
        new OverlayOffsetAlign(AlignModeX.CENTER, AlignModeY.MIDDLE,
            new TextImage("MAGENTA WINS!", 20, FontStyle.BOLD, Color.MAGENTA), 0, -30,
            new StarImage(200, OutlineMode.SOLID, Color.YELLOW)));

    test2.placeImageXY(magImg, 550, 450);

    t.checkExpect(this.game1.endImage(false), test2);
  }
  
  // test onKeyEvent
  void testOnKey(Tester t) {
    this.initData();
    this.game1.cells = new ArrayList<Cell>();
    this.game1.pinkTurn = false;
    this.game1.pinkWin = true;
    t.checkExpect(this.game1.cells, new ArrayList<Cell>());
    t.checkExpect(this.game1.pinkTurn, false);
    t.checkExpect(this.game1.pinkWin, true);
    
    this.game1.onKeyEvent("r");
    
    t.checkExpect(this.game1.cells.size(), 25);
    t.checkExpect(this.game1.pinkTurn, true);
    t.checkExpect(this.game1.pinkWin, false);
  }

  // tests Big Bang
  void testBigBang(Tester t) {
    BridgIt game = new BridgIt(9);
    int worldSize = 900;
    double tickRate = 0.2;
    game.bigBang(worldSize + 200, worldSize, tickRate);
  }
}

// we will be using our late days
