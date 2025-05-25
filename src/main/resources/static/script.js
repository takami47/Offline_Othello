window.onload = async function() {
    // Spring Boot の API から盤のデータを取得
    const response = await fetch("/shogi/board");
    const board = await response.json();  // 盤のデータをJSON形式で取得

    // Canvas の設定
    const canvas = document.getElementById("shogiCanvas");
    const ctx = canvas.getContext("2d");
    const cellSize = 50;  // 1マスのサイズ

    // 盤の描画
    for (let y = 0; y < 9; y++) {
        for (let x = 0; x < 9; x++) {
            // 1マスを描画
            ctx.strokeRect(x * cellSize, y * cellSize, cellSize, cellSize);

            // 盤の文字（駒）を描画
            ctx.fillStyle = "#000";  // 文字の色
            ctx.font = "20px Arial";  // フォントサイズ
            ctx.fillText(board[y][x], x * cellSize + 15, y * cellSize + 30);  // 文字を描画
        }
    }
};
