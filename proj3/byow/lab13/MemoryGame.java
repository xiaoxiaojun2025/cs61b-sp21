package byow.lab13;

import static byow.Core.RandomUtils.*;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

import javax.sound.sampled.*;


public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!", "You will die!!!"};
    private static final String[] NOTE = {"每一轮浮现一个字符串，之后按随机顺序出现字符串中的字符，记住并按该顺序打出字符串",
            "按随机出现的顺序输入而不是初始顺序", "输错后无法回头！！！谨慎一点？！", "不要失败......."};
    private static final String[] HINT = {"Type!!!", "Watch"};
    private static final Font smallFont = new Font("Monaco", Font.BOLD, 15);
    private static final Font defaultFont = new Font("Monaco", Font.BOLD, 30);
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(80, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder stringBuilder = new StringBuilder();
        char c;
        for (int i = 0; i < n; ++i) {
            c = CHARACTERS[uniform(rand, CHARACTERS.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public void drawFrame(String s) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 20, s);
        drawHeader(true);
        StdDraw.show();
        StdDraw.pause(1000);
        StdDraw.clear(Color.BLACK);
        drawHeader(true);
        StdDraw.show();
        StdDraw.pause(1000);
    }

    public String flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        char[] shuffleLetters = letters.toCharArray();
        shuffle(rand, shuffleLetters);
        for (int i = 0; i < shuffleLetters.length; ++i) {
            StdDraw.clear(Color.BLACK);
            StdDraw.text(40, 20, Character.toString(shuffleLetters[i]));
            drawHeader(true);
            StdDraw.show();
            StdDraw.pause(1000);
            StdDraw.clear(Color.BLACK);
            drawHeader(true);
            StdDraw.show();
            StdDraw.pause(500);
        }
        return new String(shuffleLetters);
    }

    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        cleanKeys();
        StdDraw.clear(Color.BLACK);
        drawHeader(false);
        StdDraw.show();
        StdDraw.pause(5);
        int i = 0;
        StringBuilder currString = new StringBuilder();
        char c;
        while (i < n) {
            if (StdDraw.hasNextKeyTyped()) {
                StdDraw.pause(50);
                StdDraw.clear(Color.BLACK);
                c = Character.toLowerCase(StdDraw.nextKeyTyped());
                currString.append(c);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(width / 2, height / 2, currString.toString());
                drawHeader(false);
                StdDraw.show();
                StdDraw.pause(50);
                ++i;
            }
        }
        return currString.toString();
    }

    public void startGame() {
        //TODO: Set any relevant variables before the game starts

        //TODO: Establish Engine loop
        gameOver = false;
        round = 1;
        drawNotes();
        while (!gameOver) {
            int size = round;
            String originalString = generateRandomString(size);
            drawFrame(originalString);
            String flashedString = flashSequence(originalString);
            String typedString = solicitNCharsInput(size);
            if (!typedString.equals(flashedString)) {
                drawGameOver();
                StdDraw.disableDoubleBuffering();
                System.exit(0);
                gameOver = true;
            }
            StdDraw.pause(1000);
            StdDraw.clear(Color.BLACK);
            drawHeader(true);
            StdDraw.show();
            StdDraw.pause(1000);
            ++round;
        }
    }
    private static void cleanKeys() {
        while (StdDraw.hasNextKeyTyped()) {
            StdDraw.nextKeyTyped();
            StdDraw.pause(5);
        }
    }
    private void drawHeader(boolean isWatching) {
        StdDraw.textLeft(2.5, height - 2.5, "Round: " + round);
        String encouragement = ENCOURAGEMENT[uniform(rand, ENCOURAGEMENT.length)];
        int temp = isWatching ? 1 : 0;
        StdDraw.text(width / 2, height - 2.5, HINT[temp]);
        StdDraw.textRight(width - 2.5, height - 2.5, encouragement);
    }
    private void drawNotes() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(width / 2, height / 2 + 3, "记忆游戏!!!");
        StdDraw.setFont(smallFont);
        StdDraw.text(width / 2, height / 2, NOTE[0]);
        StdDraw.text(width / 2, height / 2 - 2.5, NOTE[1]);
        StdDraw.text(width / 2, height / 2 - 5, NOTE[2]);
        StdDraw.text(width / 2, height / 2 - 7.5, NOTE[3]);
        StdDraw.show();
        StdDraw.setFont(defaultFont);
        StdDraw.setPenColor(Color.RED);
        StdDraw.pause(8000);
    }
    private void drawGameOver() {
        StdDraw.clear();
        StdDraw.setPenColor(Color.RED);
        StdDraw.text(width / 2, height - 3, "你被道理抓到了！！！");
        StdDraw.picture(width / 2, height / 2, "images/shuodedaoli.png", width, height);
        StdDraw.show();
        playSound("./waao.wav");
        StdDraw.pause(5000);
    }

    public static void playSound(String audioPath) {
        try {
            // 获取音频文件
            java.io.File audioFile = new java.io.File(audioPath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // 获取音频格式并创建Clip
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip audioClip = (Clip) AudioSystem.getLine(info);

            // 打开并播放音频
            audioClip.open(audioStream);
            audioClip.start();

            // 注意：不要关闭clip，否则音频会停止

        } catch (Exception e) {
            System.err.println("播放音频失败: " + e.getMessage());
        }
    }

}
