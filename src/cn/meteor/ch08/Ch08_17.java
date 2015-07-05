package cn.meteor.ch08;

import com.sun.awt.AWTUtilities;
import javazoom.jl.player.Player;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by meteor on 15-6-23.
 */
public class Ch08_17 {
    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        MainView mainView = new MainView();
        //AWTUtilities.setWindowOpacity(mainView, 0.1f);
        mainView.setVisible(true);

    }
}

class MainView extends JFrame implements MouseListener {
    private static final Font keyFont = new Font(null, Font.BOLD, 20);
    private static final Font valFont = new Font(null, Font.ITALIC, 15);
    JScrollPane jsp;
    ArrayList<Music> musicList = new ArrayList<Music>();
    JList<Music> musicJList;
    private JSplitPane splitPane;
    private String filePath;
    private Music currentMusic;
    private JLabel jlbSongImage, jlbCurrentMusic, jlbCurrentMusicText;
    private JLabel jlbMusicName, jlbAuthor, jlbSinger;
    private JLabel jlbMusicNameText, jlbAuthorText, jlbSingerText;
    private MusicPlayThread musicPlayThread;
    private JPanel controlPanel, showPanel, showPanelLeft, showPanelRight, musicListPanel;
    private JMenuBar menuBar;
    private JMenu fileMenu, helpMenu;
    private JMenuItem fileMenuOpen, fileMenuOpenDir, fileMenuExit, helpMenuSoftInfo, helpMenuAbout;
    private JButton btnPlay, btnPause, btnStop, btnScan;
    private MyListener myListener = new MyListener();
    private JLabel jlbCurrentTime, jlbTotalTime;
    private JTextField systemTime;

    public MainView() {

        menuBar = new JMenuBar();
        fileMenu = new JMenu("文件");
        fileMenuOpen = new JMenuItem("打开音乐文件");
        fileMenuOpenDir = new JMenuItem("打开音乐文件文件夹");
        fileMenuExit = new JMenuItem("退出");
        helpMenu = new JMenu("帮助");
        helpMenuSoftInfo = new JMenuItem("软件信息");
        helpMenuAbout = new JMenuItem("关于作者");


        controlPanel = new JPanel();
        btnPlay = new JButton("开始");
        btnPause = new JButton("暂停");
        btnStop = new JButton("停止");
        btnScan = new JButton("扫描");
        jlbCurrentTime = new JLabel("0:0");
        jlbTotalTime = new JLabel("0:0");
        systemTime = new JTextField();
        systemTime.setEditable(false);
        systemTime.setForeground(Color.RED);
        systemTime.setBackground(Color.WHITE);
        controlPanel.add(btnScan);
        controlPanel.add(btnPlay);
        controlPanel.add(btnPause);
        controlPanel.add(btnStop);
        controlPanel.add(jlbCurrentTime);
        controlPanel.add(jlbTotalTime);
        controlPanel.add(systemTime);


        showPanel = new JPanel();
        showPanelLeft = new JPanel();
        showPanelRight = new JPanel();
        jlbSongImage = new JLabel(new ImageIcon("default.jpg"));
        showPanelLeft.setLayout(new GridLayout(1, 1));
        showPanelLeft.add(jlbSongImage);
        showPanel.setLayout(new GridLayout(2, 2));
        showPanel.add(showPanelLeft);

        showPanel.add(showPanelRight);
        jlbSinger = new JLabel("歌手 :");
        jlbMusicName = new JLabel("歌名 :");
        jlbAuthor = new JLabel("专辑 :");
        jlbSingerText = new JLabel("--");
        jlbMusicNameText = new JLabel("--");
        jlbAuthorText = new JLabel("--");
        jlbCurrentMusic = new JLabel("      正在播放...: ");


        //音乐列表
        musicListPanel = new JPanel();

        musicJList = new JList<Music>();
        musicJList.setToolTipText("音乐列表");
        musicListPanel.add(musicJList);
        jsp = new JScrollPane(musicListPanel);
        splitPane = new JSplitPane(1, true, jsp, showPanel);
        splitPane.setDividerLocation(200);


        showPanelRight.setLayout(new GridLayout(3, 2));
        showPanelRight.add(jlbMusicName);
        showPanelRight.add(jlbMusicNameText);
        showPanelRight.add(jlbSinger);
        showPanelRight.add(jlbSingerText);
        showPanelRight.add(jlbAuthor);
        showPanelRight.add(jlbAuthorText);
        showPanel.add(showPanelLeft);
        showPanel.add(showPanelRight);
        showPanel.add(jlbCurrentMusic);
        jlbCurrentMusicText = new JLabel("    --");
        jlbCurrentMusicText.setBackground(Color.green);
        showPanel.add(jlbCurrentMusicText);

        btnPlay.addActionListener(myListener);
        btnPlay.setActionCommand("play");
        btnPause.addActionListener(myListener);
        btnPause.setActionCommand("pause");
        btnStop.setActionCommand("stop");
        btnStop.addActionListener(myListener);
        btnScan.setActionCommand("scan");
        btnScan.addActionListener(myListener);
        fileMenu.add(fileMenuOpen);
        fileMenu.add(fileMenuOpenDir);
        fileMenu.add(fileMenuExit);
        helpMenu.add(helpMenuSoftInfo);
        helpMenu.add(helpMenuAbout);
        menuBar.add(fileMenu);
        fileMenuOpen.addActionListener(myListener);
        fileMenuOpen.setActionCommand("open");
        fileMenuOpenDir.setActionCommand("opendir");
        fileMenuOpenDir.addActionListener(myListener);
        fileMenuExit.addActionListener(myListener);
        fileMenuExit.setActionCommand("exit");
        helpMenuSoftInfo.addActionListener(myListener);
        helpMenuSoftInfo.setActionCommand("info");
        helpMenuAbout.addActionListener(myListener);
        helpMenuAbout.setActionCommand("about");
        menuBar.add(helpMenu);

        musicJList.setForeground(Color.green);
        musicJList.addMouseListener(this);

        //font
        jlbCurrentMusicText.setFont(valFont);
        jlbCurrentMusicText.setForeground(Color.red);
        jlbAuthor.setFont(keyFont);
        jlbAuthorText.setFont(valFont);
        jlbMusicName.setFont(keyFont);
        jlbMusicNameText.setFont(valFont);
        jlbSinger.setFont(keyFont);
        jlbSingerText.setFont(valFont);
        jlbCurrentMusic.setFont(keyFont);
        jlbCurrentMusicText.setFont(valFont);


        this.add(splitPane, BorderLayout.CENTER);
        this.add(menuBar, BorderLayout.NORTH);
        this.add(controlPanel, BorderLayout.SOUTH);
        loadConfig();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setSize(600, 400);
        this.setTitle("Meteor's Music Player");
        System.out.println(jlbCurrentMusicText.getX() + "," + jlbCurrentMusicText.getY());
        this.setUndecorated(true);

        setResizable(false);
        rollzCurrentMusic();
        refreshCurrentPlayTime();
       // this.setVisible(true);
    }

    private void loadConfig() {
        if (!Util.readConfigFromDisk().equals(""))
            loadDir(Util.readConfigFromDisk());
        if (Util.readObjectsFromDisk() != null) {
            musicList.clear();
            System.out.println(musicJList);
            musicList = Util.readObjectsFromDisk();
            refreshMusicList();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("..." + e.getSource());

        if (e.getClickCount() == 2) {
            System.out.println("...");
            if (musicPlayThread != null)
                musicPlayThread.stop();
            currentMusic = musicJList.getSelectedValue();
            filePath = currentMusic.getFilePath();
            currentMusic.loadFile();
            jlbSingerText.setText(currentMusic.getSinger());
            jlbMusicNameText.setText(currentMusic.getSongName());
            jlbAuthorText.setText(currentMusic.getAuthor());
            jlbSongImage.setIcon(currentMusic.getICon());
            jlbCurrentMusicText.setText("    " + currentMusic.getSongName());
            if (musicPlayThread != null)
                musicPlayThread.getMp3Player().stop();
            musicPlayThread = new MusicPlayThread(currentMusic);
            musicPlayThread.start();
        }

        if (e.getClickCount() == 1) {
            currentMusic = musicJList.getSelectedValue();
        }


    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }


    class MyListener implements ActionListener {
        private boolean paused = false;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("play")) {
                if (musicPlayThread != null)
                    musicPlayThread.getMp3Player().stop();

                currentMusic = new Music(filePath);
                currentMusic.loadFile();
                jlbSingerText.setText(currentMusic.getSinger());
                jlbMusicNameText.setText(currentMusic.getSongName());
                jlbAuthorText.setText(currentMusic.getAuthor());
                jlbSongImage.setIcon(currentMusic.getICon());
                jlbCurrentMusicText.setText("    " + currentMusic.getSongName());
                musicPlayThread = new MusicPlayThread(currentMusic);
                if (musicPlayThread.getState() != Thread.State.NEW) {
                    musicPlayThread.resume();
                } else {
                    musicPlayThread.start();
                }
            }
            if (e.getActionCommand().equals("pause")) {
                try {
                    if (paused) {
                        musicPlayThread.getMp3Player().resume();
                        paused = false;
                        btnPause.setText("暂停");
                        // refreshCurrentPlayTime();
                    } else {
                        musicPlayThread.getMp3Player().pause();
                        paused = true;
                        btnPause.setText("继续");
                    }
                } catch (Exception e1) {
                    throw new RuntimeException(e1);
                }
            }

            if (e.getActionCommand().equals("open")) {
                JFileChooser jFileChooser = new JFileChooser("");
                jFileChooser.showOpenDialog(MainView.this);
                filePath = jFileChooser.getSelectedFile().getAbsolutePath();
                System.out.println(filePath);
            }

            if (e.getActionCommand().equals("opendir")) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.showOpenDialog(MainView.this);
                String dirPath = jFileChooser.getSelectedFile().getAbsolutePath();
                System.out.println(dirPath);
                loadDir(dirPath);
                Util.setDirPath(dirPath);
                Util.writeConfigToDisk();
                repaint();
            }

            if (e.getActionCommand().equals("stop")) {
                if (musicPlayThread != null) {
                    musicPlayThread.getMp3Player().stop();
                    musicPlayThread = null;
                    repaint();
                }
            }

            if (e.getActionCommand().equals("scan")) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jFileChooser.setDialogTitle("请选择扫描根节点目录");
                jFileChooser.showOpenDialog(MainView.this);
                File rootDir = jFileChooser.getSelectedFile();
                ArrayList<Music> musics = new ArrayList<Music>();
                if (rootDir != null) {
                    Scan(rootDir);
                    refreshMusicList();
                    Util.writeObjectsToDisk(musicList);
                }

            }

            if (e.getActionCommand().equals("exit")) {
                System.exit(0);
            }

            if (e.getActionCommand().equals("info")) {
                JOptionPane.showMessageDialog(MainView.this, "音乐播放器1.0");
            }

            if (e.getActionCommand().equals("about")) {
                JOptionPane.showMessageDialog(MainView.this, "段启岩");
            }
        }

    }

    protected void refreshMusicList() {
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < musicList.size(); i++) {
            listModel.add(i, musicList.get(i));
        }
        musicJList.setModel(listModel);
    }

    protected void refreshCurrentPlayTime() {
        new Thread(new Runnable() {
            private int time = 0;
            private DateFormat format = new SimpleDateFormat("mm:ss");

            @Override
            public void run() {
                while (true) {
                    systemTime.setText("系统时间 : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));

                    try {
                        Thread.sleep(900);
                        if (musicPlayThread != null) {
                            time = musicPlayThread.getCurrentTime();
                            jlbCurrentTime.setText(format.format(time));
                            jlbTotalTime.setText(format.format(currentMusic.getTrackLength()));
                        }
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }).start();
    }

    protected void Scan(File rootDir) {
        File[] files = rootDir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory())
                    Scan(files[i]);
                else if (files[i].getAbsolutePath().endsWith(".mp3"))
                    musicList.add(new Music(files[i].getAbsolutePath()));
            }
        }
    }

    protected void loadObjects() {
        musicList.clear();
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < musicList.size(); i++) {
            listModel.add(i, musicList.get(i));
        }
        musicJList.setModel(listModel);
    }

    protected void loadDir(String dirPath) {
        musicList.clear();
        File f = new File(dirPath);
        File[] files = f.listFiles();
        if (files != null)
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(".mp3"))
                    musicList.add(new Music(files[i].getAbsolutePath()));
                else
                    System.out.println(files[i].getAbsolutePath());
            }
        //新建一个默认项集合
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < musicList.size(); i++) {
            listModel.add(i, musicList.get(i));
        }
        musicJList.setModel(listModel);
    }

    private void rollzCurrentMusic() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isShow = true;
                while (true) {
                    try {
                        Thread.sleep(1000);

                        if (isShow) {
                            jlbCurrentMusicText.setEnabled(false);
                            isShow = false;
                        } else {
                            jlbCurrentMusicText.setEnabled(true);
                            isShow = true;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}


class Util {
    private static String objectConfigPath = "obj.plist";
    private static String dirPath = "";
    private static final String configFilePathh = "config.plist";

    public Util(String dirPath) {
        this.dirPath = dirPath;
    }

    public static void writeConfigToDisk() {
        File file = new File(configFilePathh);
        OutputStream ops = null;
        try {
            System.out.println(file.getAbsolutePath());
            ops = new FileOutputStream(file);
            ops.write(dirPath.getBytes());
            ops.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ops.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void writeObjectsToDisk(ArrayList<Music> musics) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(objectConfigPath));
            oos.writeObject(musics);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ArrayList<Music> readObjectsFromDisk() {
        ObjectInputStream ois = null;
        ArrayList<Music> obj = null;
        try {
            File file = new File(objectConfigPath);
            if (file.exists()) {
                ois = new ObjectInputStream(new FileInputStream(objectConfigPath));
                obj = (ArrayList<Music>) (ois.readObject());
                System.out.println(obj);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return obj;
    }

    public static String readConfigFromDisk() {
        File file = new File("config.plist");
        BufferedReader bfr = null;
        try {
            System.out.println(file.getAbsolutePath());
            if (file.exists()) {
                bfr = new BufferedReader(new InputStreamReader(new FileInputStream(configFilePathh)));
                dirPath = bfr.readLine().toString();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bfr != null)
                    bfr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dirPath.trim();
    }

    public static String getDirPath() {
        return dirPath;
    }

    public static void setDirPath(String dirPath1) {
        dirPath = dirPath1;
    }
}

class MusicPlayThread extends Thread {
    private boolean paused = false;
    private Music music;
    private MP3Player mp3Player;

    public MusicPlayThread(Music music) {
        this.music = music;
        this.mp3Player = new MP3Player(music.getFilePath());
    }

    @Override
    public void run() {
        mp3Player.play(-1);
    }


    public int getCurrentTime() {
        return mp3Player.getPosition();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public MP3Player getMp3Player() {
        return mp3Player;
    }

    public void setMp3Player(MP3Player mp3Player) {
        this.mp3Player = mp3Player;
    }
}

class MP3Player {
    private boolean havePaused = false;

    public MP3Player(String filename) {
        this.filename = filename;
        player = null;
        FIS = null;
        valid = false;
        BIS = null;
        total = 0;
        stopped = 0;
        canResume = false;
    }

    //    public void play() {
//        try {
//            BufferedInputStream buffer = new BufferedInputStream(
//                    new FileInputStream(filename));
//            player = new Player(buffer);
//            player.play(2147483647);
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//    }
    public int getPosition() {
        if (player != null) {
            if (havePaused)
                return player.getPosition() + stopped;
            else return player.getPosition();
        }
        return 0;
    }

    private String filename;
    private Player player;

    private FileInputStream FIS;
    private BufferedInputStream BIS;
    private boolean canResume;
    private int total;
    private int stopped;
    private boolean valid;

    public boolean canResume() {
        return canResume;
    }

    public void pause() {
        try {
            stopped = FIS.available();
            player.close();
            FIS = null;
            BIS = null;
            player = null;
            if (valid) canResume = true;
            havePaused = true;
        } catch (Exception e) {
        }
    }

    public void resume() {
        if (!canResume) return;
        if (play(total - stopped)) canResume = false;
    }

    public void stop() {
        havePaused = false;
        if (null != player)
        player.close();
    }

    public boolean play(int pos) {
        havePaused = false;
        valid = true;
        canResume = false;
        try {
            FIS = new FileInputStream(filename);
            total = FIS.available();
            if (pos > -1) FIS.skip(pos);
            BIS = new BufferedInputStream(FIS);
            player = new Player(BIS);
            new Thread(
                    new Runnable() {
                        public void run() {
                            try {
                                player.play();
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error playing mp3 file");
                                valid = false;
                            }
                        }
                    }
            ).start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error playing mp3 file");
            valid = false;
        }
        return valid;
    }

}

class Music implements Serializable {
    private int totalTime;
    private String filePath;
    private MP3File file;

    public Music(String filename) {
        this.filePath = filename;
        //loadFile();
    }

    public void loadFile() {
        try {
            file = new MP3File(filePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getSongName() {
        String text = file.getID3v2Tag().frameMap.get("TIT2").toString();
        return text.substring(6, text.length() - 3);
    }

    public int getTrackLength() {
        if (file != null)
            return file.getAudioHeader().getTrackLength() * 1000;
        else
            return 0;
    }

    public String getSinger() {
        Object obj = file.getID3v2Tag().frameMap.get("TPE1");
        if (obj == null)
            return "未知";
        else
            return obj.toString().substring(6, obj.toString().length() - 3);
    }

    public String getAuthor() {
        String text = file.getID3v2Tag().frameMap.get("TALB").toString();
        return text.substring(6, text.length() - 3);
    }

    public Image getImage() {
        AbstractID3v2Tag tag = file.getID3v2Tag();
        AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");
        FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
        byte[] imageData = body.getImageData();
        Image img = Toolkit.getDefaultToolkit().createImage(imageData, 0, imageData.length);
        return img;
    }

    public ImageIcon getICon() {
        AbstractID3v2Tag tag = file.getID3v2Tag();
        AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");
        FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
        byte[] imageData = body.getImageData();
        ImageIcon imageIcon = new ImageIcon(imageData);
        imageIcon.setImage(this.getImage().getScaledInstance(180, 180,
                Image.SCALE_DEFAULT));
        return imageIcon;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return new File(filePath).getName();
    }
}