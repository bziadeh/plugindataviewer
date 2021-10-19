package com.ziadeh;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

class ExampleMain {
    public static void main(String[] args) {
        PluginDataViewer dataViewer = new PluginDataViewer();

        ExampleObject myObject = new ExampleObject();
        dataViewer.addObject(myObject);

        AnotherExample anotherExample = new AnotherExample();
        dataViewer.addObject(anotherExample);
    }
}

public class PluginDataViewer extends JFrame {

    private final Map<Object, Integer> objectsToTrack = new HashMap<>();
    private final DefaultTableModel data = new DefaultTableModel();

    public PluginDataViewer() {
        final Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("Plugin Data Viewer");
        setSize(resolution.width / 2, resolution.height / 2);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTable table = new JTable(data);
        table.setRowHeight(30);
        table.setFont(new Font("Serif", Font.BOLD, 20));
        add(table);
        setVisible(true);
        updateTable(500);
    }

    private Object[] createRow(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        Object[] row = new Object[fields.length + 1];
        row[0] = object.toString(); // name of the object.
        for(int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            final String fieldName = field.getName();
            try {
                row[i + 1] = fieldName + ": " + field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return row;
            }
        }
        return row;
    }

    private void updateTable(long millis) {
        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for(Map.Entry<Object, Integer> entrySet : objectsToTrack.entrySet()) {
                    int row = entrySet.getValue();
                    data.removeRow(row);
                    data.insertRow(row, createRow(entrySet.getKey()));
                }
                data.fireTableDataChanged();
            }
        }).start();
    }

    public void addObject(Object objectToTrack) {
        Object[] row = createRow(objectToTrack);

        if(data.getColumnCount() < row.length)
            data.setColumnCount(row.length);

        data.addRow(row);
        data.fireTableDataChanged();

        int rowNumber = data.getRowCount() - 1;
        objectsToTrack.put(objectToTrack, rowNumber);
    }
}
