package org.interactunes.screensaver.frames;

import org.interactunes.screensaver.utils.IDisposable;
import org.interactunes.screensaver.utils.IShowable;
import org.interactunes.screensaver.utils.UtilMethods;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;

/**
 * A frame that displays the log.
 */
public class LogFrame implements IShowable, IDisposable {

    private final JFrame frame;
    private final DefaultListModel<String> logListModel;
    private final LinkedList<String> logQueue;
    private final int maxCapacity;

    /**
     * Creates a new log frame.
     *
     * @param maxCapacity The maximum amount of logs to display before removing the oldest log.
     */
    public LogFrame(int maxCapacity) {
        this.maxCapacity = maxCapacity;

        frame = new JFrame("Log");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setMinimumSize(new Dimension(750, 600));
        frame.setLocationRelativeTo(null);

        logListModel = new DefaultListModel<>();
        logQueue = new LinkedList<>();
        JList<String> logs = new JList<>(logListModel);
        logs.setFont(new Font("Arial", Font.PLAIN, UtilMethods.pointToPixel(12)));
        logs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        logs.setLayoutOrientation(JList.VERTICAL);
        logs.setVisibleRowCount(4);

        JScrollPane listScroller = new JScrollPane(logs);
        listScroller.setPreferredSize(new Dimension(750, 600));
        frame.add(listScroller);
    }

    @Override
    public void show() {
        frame.setVisible(true);
    }

    @Override
    public void dispose() {
        frame.dispose();
    }

    /**
     * Adds a log to the log frame.
     *
     * @param log The log to add.
     */
    public void addLog(String log) {
        if (logQueue.size() >= maxCapacity) {
            String removedLog = logQueue.removeFirst();
            logListModel.removeElement(removedLog);
        }
        logQueue.addLast(log);
        logListModel.addElement(log);
    }
}
