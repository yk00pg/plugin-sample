package plugin.sample;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class Main extends JavaPlugin implements Listener {

  private BigInteger sneakCount = new BigInteger("1");

  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
  }

  /**
   * プレイヤーがスニークを開始/終了する際に起動されるイベントハンドラ。
   *
   * @param e イベント
   */
  @EventHandler
  public void onPlayerToggleSneak (PlayerToggleSneakEvent e) throws IOException {
    // イベント発生時のプレイヤーやワールドなどの情報を変数に持つ。
    Player player = e.getPlayer();
    World world = player.getWorld();

    List<Type> typeList = List.of(Type.BALL, Type.STAR, Type.BALL_LARGE);

    // スニークした回数が素数のときに実行する。
    if(sneakCount.isProbablePrime(1)) {
      int delay = 0; // 遅延時間を管理する変数

      for(Type type :typeList) {
        // 遅延を加えて花火を打ち上げるためのBukkitRunnable
        new BukkitRunnable() {
          @Override
          public void run() {
            // 花火オブジェクトをプレイヤーのロケーション地点に対して出現させる。
            Firework firework = world.spawn(player.getLocation(), Firework.class);

            // 花火オブジェクトが持つメタ情報を取得。
            FireworkMeta fireworkMeta = firework.getFireworkMeta();

            // メタ情報に対して設定を追加したり、値の上書きを行う。
            fireworkMeta.addEffect(
                FireworkEffect.builder()
                    .withColor(Color.FUCHSIA)
                    .withColor(Color.AQUA)
                    .with(type)
                    .withFlicker()
                    .build());
            fireworkMeta.setPower(((2+3)*4)/10);

            // 追加した情報で再設定する。
            firework.setFireworkMeta(fireworkMeta);
          }
        }.runTaskLater(this, delay);  // delay秒後に実行
        delay += 10;  // 0.5秒遅延を追加（20ティック＝1秒）
      }
      Path path = Path.of("firework.txt");
      if(sneakCount.toString().contains("3")) {
        Files.writeString(path, "かーぎやー!");  // 3がつく回数のときは「かーぎやー！」と表示
      }else{
        Files.writeString(path, "たーまやー!");  // それ以外のときは「たーまやー！」と表示
      }
      // アクションバーにメッセージを表示
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Files.readString(path)));
      player.sendMessage(sneakCount + "は素数なので花火が飛びました");
      // 何回後に花火が飛ぶか表示
      player.sendMessage("次に花火が飛ぶのは"+ sneakCount.nextProbablePrime().subtract(sneakCount)+"回後です");
    }
    sneakCount = sneakCount.add(BigInteger.ONE);
  }
}