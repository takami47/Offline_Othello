import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

public class Othello extends Application {
    private static final int BOX_COUNT = 8;
    private static final int CELL_SIZE = 70;
    private final char[][] board = new char[BOX_COUNT][BOX_COUNT];
    private char currentPlayer = 'B';
    private GridPane visualBoard;
    private Label scoreLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        visualBoard = new GridPane();
        scoreLabel = new Label();
        initBoard();
        updateBoard();
        VBox root = new VBox(5);// 縦並びに変更
        root.getChildren().addAll(scoreLabel, visualBoard);

        // スコアラベルの見た目
        scoreLabel.setStyle("-fx-font-size: 20px;");

        stage.setScene(new Scene(root));
        stage.setTitle("オフラインオセロ");
        stage.show();
    }

    private void initBoard() {
        for (int i = 0; i < BOX_COUNT; i++)
            for (int j = 0; j < BOX_COUNT; j++)
                board[i][j] = '.';
        board[3][3] = board[4][4] = 'W';
        board[3][4] = board[4][3] = 'B';
    }

    private void updateBoard() {
        visualBoard.getChildren().clear();  // 各マスのメモリが気になるならこのコードを入れる（全マス初期化コード）

        for (int col = 0; col < BOX_COUNT; col++) {           // ← 行（縦方向）のループ
            for (int row = 0; row < BOX_COUNT; row++) {       // ← 列（横方向）のループ
                StackPane cell = createCell(col, row);   // ← マス（StackPane）を作成、クリック処理も登録される

                if (board[col][row] == 'B' || board[col][row] == 'W') {     // ← そのマスに黒か白の石があるなら
                    cell.getChildren().add(createKoma(board[col][row]));   // ← 石の見た目（Circle）を追加
                }

                visualBoard.add(cell, row, col);  // ← できたマスを盤面（GridPane）に配置する
            }
        }

        // ↓ 黒と白の石の数をカウントしてスコア表示を更新する部分

        int black = 0, white = 0;  // ← 黒と白のカウント変数を用意
        for (int i = 0; i < BOX_COUNT; i++) {          // ← 行のループ
            for (int j = 0; j < BOX_COUNT; j++) {      // ← 列のループ
                if (board[i][j] == 'B') black++;  // ← 黒の石をカウント
                if (board[i][j] == 'W') white++;  // ← 白の石をカウント
            }
        }

        scoreLabel.setText("黒: " + black + "\n白: " + white);  // ← スコアラベルに現在の黒白の数を表示
    }

    private StackPane createCell(int col, int row) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);
        cell.setStyle("-fx-border-color: black; -fx-background-color: green;");

        cell.setOnMouseClicked((MouseEvent e) -> {
            if (board[col][row] != '.') return;

            boolean canPlace = false;

            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    if (canFlip(col, row, dr, dc, currentPlayer)) {
                        canPlace = true;
                    }
                }
            }

            if (canPlace) {
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;
                        if (canFlip(col, row, dr, dc, currentPlayer)) {
                            flip(col, row, dr, dc, currentPlayer);
                        }
                    }
                }
                board[col][row] = currentPlayer;
                currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
                updateBoard();
            }
        });

        return cell;
    }


    //指定された色（'B' または 'W'）に応じて、オセロの石（円）を作成して返す
    private Circle createKoma(char color) {
        // 石のサイズを決めて円（Circle）オブジェクトを作成（半径 = マスの38%）
        Circle spec = new Circle(CELL_SIZE * 0.38);

        // 色の設定：'B'（黒）なら黒い石、'W'（白）なら白い石を作る
        spec.setFill(color == 'B' ? Color.BLACK : Color.WHITE);

        // 作成した石（円）を返す
        return spec;
    }

    private boolean canFlip(int col, int row, int deltaRow, int deltaCol, char color) {
        int i = col + deltaRow;
        int j = row + deltaCol;
        boolean seenOpponent = false;

        while (i >= 0 && i < BOX_COUNT && j >= 0 && j < BOX_COUNT) {
            char current = board[i][j];
            if (current == '.') return false;
            if (current == color) return seenOpponent;
            seenOpponent = true;
            i += deltaRow;
            j += deltaCol;
        }
        return false;
    }

    private void flip(int col, int row, int deltaRow, int deltaCol, char color) {
        int i = col + deltaRow;
        int j = row + deltaCol;

        while (i >= 0 && i < BOX_COUNT && j >= 0 && j < BOX_COUNT && board[i][j] != color) {
            board[i][j] = color;
            i += deltaRow;
            j += deltaCol;
        }
    }

}
