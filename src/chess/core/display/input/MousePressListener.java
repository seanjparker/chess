package chess.core.display.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface MousePressListener extends MouseListener {
  @Override
  default public void mouseEntered(final MouseEvent e) {
  }

  @Override
  default public void mouseExited(final MouseEvent e) {
  }

  @Override
  default public void mousePressed(final MouseEvent e) {
  }

  @Override
  default public void mouseReleased(final MouseEvent e) {
  }
}
