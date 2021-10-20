package com.ziadeh;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class ExampleMain {
    public static void main(String[] args) {
        PluginDataViewer dataViewer = new PluginDataViewer();

        ExampleObject myObject = new ExampleObject();
        dataViewer.addObject(myObject);
    }
}

public class PluginDataViewer extends JFrame {

    private final Map<Object, Integer> objectsToTrack = new HashMap<>();
    private final DefaultTableModel data = new DefaultTableModel();

    public PluginDataViewer() {
        final Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("Debug Window");
        setSize(resolution.width / 2, resolution.height / 2);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JTable table = new JTable(data);
        table.setRowHeight(30);
        table.setFont(new Font("Serif", Font.BOLD, 20));
        add(table);
        setVisible(true);
        updateTable(500);
    }

    private String getValue(Object objectToTrack, Field field) throws IllegalAccessException, InvocationTargetException {
        Object obj = field.get(objectToTrack);

        Method[] methods;
        if((methods = obj.getClass().getDeclaredMethods()).length == 0)
            return obj.toString();

        for(Method method : methods) {
            String name = method.getName().toLowerCase();
            if(name.equals("getname") || name.equals("gettag") || name.equals("name"))
                return method.invoke(obj).toString();
        }
        return obj.toString();
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
                row[i + 1] = fieldName + ": " + getValue(object, field);
            } catch (IllegalAccessException | InvocationTargetException e) {
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

    public void addObject(Object object) {
        Object[] row = createRow(object);
        if(data.getColumnCount() < row.length)
            data.setColumnCount(row.length);

        data.addRow(row);
        data.fireTableDataChanged();

        int rowNumber = data.getRowCount() - 1;
        objectsToTrack.put(object, rowNumber);
    }

    public void removeObject(Object object) {
        if(!objectsToTrack.containsKey(object))
            return;
        data.removeRow(objectsToTrack.remove(object));
        data.fireTableDataChanged();
    }
}
