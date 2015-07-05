//package cn.meteor.ch08;
//
//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.player.AudioDevice;
//import javazoom.jl.player.Player;
//
//import javax.swing.*;
//import java.io.BufferedInputStream;
//import java.io.FileInputStream;
//import java.io.InputStream;
//
///**
// * Created by meteor on 15-6-24.
// */
//public class Test {
//}
//public class PausablePlayer {
//    private final static int NOTSTARTED = 0;
//    private final static int PLAYING = 1;
//    private final static int PAUSED = 2;
//    private final static int FINISHED = 3;
//    // the player actually doing all the work
//    private final Player player;
//    // locking object used to communicate with player thread
//    private final Object playerLock = new Object();
//    // status variable what player thread is doing/supposed to do
//    private int playerStatus = NOTSTARTED;
//    public PausablePlayer(final InputStream inputStream) throws JavaLayerException {
//        this.player = new Player(inputStream);
//    }
//    public PausablePlayer(final InputStream inputStream, final AudioDevice audioDevice) throws JavaLayerException {
//        this.player = new Player(inputStream, audioDevice);
//    }
//    /**
//     * Starts playback (resumes if paused)
//     */
//    public void play() throws JavaLayerException {
//        synchronized (playerLock) {
//            switch (playerStatus) {
//                case NOTSTARTED:
//                    final Runnable r = new Runnable() {
//                        public void run() {
//                            playInternal();
//                        }
//                    };
//                    final Thread t = new Thread(r);
//                    t.setDaemon(true);
//                    t.setPriority(Thread.MAX_PRIORITY);
//                    playerStatus = PLAYING;
//                    t.start();
//                    break;
//                case PAUSED:
//                    resume();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }
//    /**
//     * Pauses playback. Returns true if new state is PAUSED.
//     */
//    public boolean pause() {
//        synchronized (playerLock) {
//            if (playerStatus == PLAYING) {
//                playerStatus = PAUSED;
//            }
//            return playerStatus == PAUSED;
//        }
//    }
//    /**
//     * Resumes playback. Returns true if the new state is PLAYING.
//     */
//    public boolean resume() {
//        synchronized (playerLock) {
//            if (playerStatus == PAUSED) {
//                playerStatus = PLAYING;
//                playerLock.notifyAll();
//            }
//            return playerStatus == PLAYING;
//        }
//    }
//    /**
//     * Stops playback. If not playing, does nothing
//     */
//    public void stop() {
//        synchronized (playerLock) {
//            playerStatus = FINISHED;
//            playerLock.notifyAll();
//        }
//    }
//    private void playInternal() {
//        while (playerStatus != FINISHED) {
//            try {
//                if (!player.play(1)) {
//                    break;
//                }
//            } catch (final JavaLayerException e) {
//                break;
//            }
//            // check if paused or terminated
//            synchronized (playerLock) {
//                while (playerStatus == PAUSED) {
//                    try {
//                        playerLock.wait();
//                    } catch (final InterruptedException e) {
//                        // terminate player
//                        break;
//                    }
//                }
//            }
//        }
//        close();
//    }
//    /**
//     * Closes the player, regardless of current state.
//     */
//    public void close() {
//        synchronized (playerLock) {
//            playerStatus = FINISHED;
//        }
//        try {
//            player.close();
//        } catch (final Exception e) {
//            // ignore, we are terminating anyway
//        }
//    }
//    // demo how to use
//    public static void main(String[] argv) {
//        try {
//            FileInputStream input = new FileInputStream("myfile.mp3");
//            PausablePlayer player = new PausablePlayer(input);
//            // start playing
//            player.play();
//            // after 5 secs, pause
//            Thread.sleep(5000);
//            player.pause();
//            // after 5 secs, resume
//            Thread.sleep(5000);
//            player.resume();
//        } catch (final Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//}
//
//2.
//        import java.io.BufferedInputStream;
//        import java.io.FileInputStream;
//        import javax.swing.JOptionPane;
//        import javazoom.jl.player.Player;
//
//public class CustomPlayer {
//    private Player player;
//    private FileInputStream FIS;
//    private BufferedInputStream BIS;
//    private boolean canResume;
//    private String path;
//    private int total;
//    private int stopped;
//    private boolean valid;
//    public CustomPlayer(){
//        player = null;
//        FIS = null;
//        valid = false;
//        BIS = null;
//        path = null;
//        total = 0;
//        stopped = 0;
//        canResume = false;
//    }
//    public boolean canResume(){
//        return canResume;
//    }
//    public void setPath(String path){
//        this.path = path;
//    }
//    public void pause(){
//        try{
//            stopped = FIS.available();
//            player.close();
//            FIS = null;
//            BIS = null;
//            player = null;
//            if(valid) canResume = true;
//        }catch(Exception e){
//        }
//    }
//    public void resume(){
//        if(!canResume) return;
//        if(play(total-stopped)) canResume = false;
//    }
//    public boolean play(int pos){
//        valid = true;
//        canResume = false;
//        try{
//            FIS = new FileInputStream(path);
//            total = FIS.available();
//            if(pos > -1) FIS.skip(pos);
//            BIS = new BufferedInputStream(FIS);
//            player = new Player(BIS);
//            new Thread(
//                    new Runnable(){
//                        public void run(){
//                            try{
//                                player.play();
//                            }catch(Exception e){
//                                JOptionPane.showMessageDialog(null, "Error playing mp3 file");
//                                valid = false;
//                            }
//                        }
//                    }
//            ).start();
//        }catch(Exception e){
//            JOptionPane.showMessageDialog(null, "Error playing mp3 file");
//            valid = false;
//        }
//        return valid;
//    }
//}
//而对于
//        CustomPlayer player = new CustomPlayer();
//        player.setPath("MP3_FILE_PATH");
//        player.play(-1);
//        然后当你想要它：
//        player.pause();
//        ......并把它：
//        player.resume();
