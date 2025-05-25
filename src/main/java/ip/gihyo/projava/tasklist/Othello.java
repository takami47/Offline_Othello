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
    private int passCount = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        visualBoard = new GridPane();
        scoreLabel = new Label();
        initBoard();// 全てのマスを'.'と認識する
        updateBoard();// 盤面の更新

        VBox root = new VBox(5);
        root.getChildren().addAll(scoreLabel, visualBoard);
        scoreLabel.setStyle("-fx-font-size: 23px; -fx-padding: 7 0 0 13;");

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
        visualBoard.getChildren().clear();// 動作を軽くするためキャッシュをクリア
        int canPlaceCount = 0;// 駒をおける場所の数

        for (int col = 0; col < BOX_COUNT; col++) {
            for (int row = 0; row < BOX_COUNT; row++) {
                StackPane cell = createCell(col, row); //全てのマスを確認する

                if (board[col][row] == 'B' || board[col][row] == 'W') {
                    cell.getChildren().add(createStone(board[col][row]));
                } else if (canPlace(col, row, currentPlayer)) {
                    Circle hint = new Circle(CELL_SIZE * 0.07); // 駒をおける場所を表示する
                    hint.setFill(Color.GRAY);
                    cell.getChildren().add(hint);
                    canPlaceCount++;
                    passCount = 0;
                }
                visualBoard.add(cell, row, col); // マスそれぞれに情報を追加する
            }
        }

        int blackStoneCount = 0, whiteStoneCount = 0; //それぞれの駒の数を数える
        for (int i = 0; i < BOX_COUNT; i++) {
            for (int j = 0; j < BOX_COUNT; j++) {
                if (board[i][j] == 'B') blackStoneCount++;
                if (board[i][j] == 'W') whiteStoneCount++;
            }
        }
        //画面上部にそれぞれの駒の数と手番が表示される
        scoreLabel.setText(
                "黒: " + blackStoneCount + "  白: " + whiteStoneCount +
                        "\n手番: " + (currentPlayer == 'B' ? "黒" : "白")
        );
        // おける場所ない場合
        if (canPlaceCount == 0) {
            turnPass(blackStoneCount, whiteStoneCount);
        }
    }

    private Circle createStone(char color) { // 黒か白の駒を作成
        Circle spec = new Circle(CELL_SIZE * 0.38);
        spec.setFill(color == 'B' ? Color.BLACK : Color.WHITE);
        return spec;
    }

    private boolean canPlace(int col, int row, char color) { //駒が置けるマスかを確認
        if (board[col][row] != '.') return false;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                if (canFlip(col, row, dr, dc, color)) return true;
            }
        }
        return false;
    }

    private boolean canFlip(int col, int row, int deltacol, int deltarow, char color) {
        int i = col + deltacol;
        int j = row + deltarow;
        boolean putCheck = false;
        while (i >= 0 && i < BOX_COUNT && j >= 0 && j < BOX_COUNT) {
            char stoneColor = board[i][j];
            if (stoneColor == '.') return false;
            if (stoneColor == color) return putCheck;
            putCheck = true;
            i += deltacol;
            j += deltarow;
        }
        return false;
    }

    private void turnPass(int black, int white) {
        passCount++;
        if (passCount == 2) {
            String result;
            if (black > white) {
                result = "黒の勝ち！";
            } else if (white > black) {
                result = "白の勝ち！";
            } else {
                result = "引き分け！";
            }
            // もう一度setTextで手番の表示を消して、試合の結果のみ表示する
            scoreLabel.setText("黒: " + black + "  白: " + white +
                    "\nゲーム終了！" + result);
        } else { // パスして相手のターン
            currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
            updateBoard();
        }
    }

    private StackPane createCell(int col, int row) {
        StackPane cell = new StackPane(); // 重ねて描けるjavafxの型
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);// それぞれのマスに緑色の背景と黒の枠線
        cell.setStyle("-fx-border-color: black; -fx-background-color: green;");
        cell.setOnMouseClicked((MouseEvent e) -> { // クリックすることで重ねて駒を配置
            if (board[col][row] != '.') return; // 駒があるところはおけない

            // 置ける場合は裏返し処理だけすればいい！
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    if (canFlip(col, row, dr, dc, currentPlayer)) { //ひっくりかえせる判定がtrueなら
                        flip(col, row, dr, dc, currentPlayer);      //ここでひっくりかえす
                    }
                }
            }
            board[col][row] = currentPlayer;
            currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
            passCount = 0;
            updateBoard();
        });
        return cell;
    }

    private void flip(int col, int row, int deltacol, int deltarow, char color) {
        int i = col + deltacol;
        int j = row + deltarow;
        //同じ方向ごとに繰り返しひっくり返す
        while (i >= 0 && i < BOX_COUNT && j >= 0 && j < BOX_COUNT && board[i][j] != color) {
            board[i][j] = color;
            i += deltacol;
            j += deltarow;
        }
    }
}
