package sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundPlay {
    private Clip clip;
    private String path;
    public SoundPlay(String path) {
        this.path = path;
        try {
            File file = new File(path);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file.getAbsoluteFile());//đọc tệp âm thanh, chuyển thành luồng dữ liệu mà hệ thống âm thanh java hiểu được
            clip = AudioSystem.getClip();//Lấy 1 đối tượng chip
            clip.open(audioInputStream);// tải dữ liệu vào đối tượng clip
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public void play() {
        clip.setFramePosition(0);// tua âm thanh về đầu để chuẩn bị cho việc phát âm thanh
        clip.start();
    }
    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);// thiết lập clip để tự phát lại khi kết thúc
    }
    public void stop() {
        clip.stop();
    }
    public boolean isFinish() {
        return (clip.getMicrosecondLength() == clip.getMicrosecondPosition());
    }//
}
