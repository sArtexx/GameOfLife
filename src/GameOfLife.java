/*
 * ������������ ������ � 1 �� ���������� "���������������� �������".
 * �������������� ��������� ����������������� �������. ���� "�����".
 * 
 * �������: ����������� ����������, ������� �������������� ���������� ���������
 * ��������� ������ "���� �����". ����������� ��������� ������������ ����, �������
 * ��������� � ���������� ���������, ��������, ���� ����������� � ��������
 * ���������.
 * 
 * �������� ������� 443 ������ ��������� ����� ��������������.
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.util.Arrays;
import java.util.Random;

public class GameOfLife {
	private final static String NAME_OF_GAME = "���� '�����'. ��������� �.�., 443 ������.";
	private final static int START_LOCATION = 200;
	private final static int LIFE_SIZE = 5;
	private final static int POINT_RADIUS = 10;
	private final static int FIELD_SIZE = 500;
	private final static int BTN_PANEL_HEIGHT = 62;
	private static boolean[][] prevGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
	private static boolean[][] lifeGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
	private static boolean[][] nextGeneration = new boolean[LIFE_SIZE][LIFE_SIZE];
	private static boolean goNextGeneration = false;
	private static int showDelay = 200;
	private static int currentGeneration = 0;
	private static Canvas canvasPanel;
	private static JLabel curGenerationLabel = new JLabel("������� ���������: " 
			+ currentGeneration);
	private static Random random = new Random();
	
	 //��������� Canvas
	@SuppressWarnings("serial")
	private static class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (int x = 0; x < LIFE_SIZE; x++) {
                for (int y = 0; y < LIFE_SIZE; y++) {
                    if (lifeGeneration[x][y]) {
                        g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, 
                        		POINT_RADIUS, POINT_RADIUS);
                    }
                }
            }
        }
    }
    
    //��������� ���������� ������
	private static class FillButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            for (int x = 0; x < LIFE_SIZE; x++) {
                for (int y = 0; y < LIFE_SIZE; y++) {
                    lifeGeneration[x][y] = random.nextBoolean();
                }
            }
            currentGeneration = 0;
            curGenerationLabel.setText("������� ���������: " 
        								+ currentGeneration);
            canvasPanel.repaint();
        }
    }
	    
	public static void main (String[] args) {
		JFrame frame = new JFrame(NAME_OF_GAME);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FIELD_SIZE, FIELD_SIZE + BTN_PANEL_HEIGHT);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);
 
        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);
        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                int x = e.getX() / POINT_RADIUS;
                int y = e.getY() / POINT_RADIUS;
                lifeGeneration[x][y] = !lifeGeneration[x][y];
                canvasPanel.repaint();
            }
        });
        
        JButton fillButton = new JButton("���������");
        fillButton.addActionListener(new FillButtonListener());
 
        JButton stepButton = new JButton("���");
        stepButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processOfLife();
                canvasPanel.repaint();
            }
        });
 
        final JButton goButton = new JButton("����");
        goButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goNextGeneration = !goNextGeneration;
                goButton.setText(goNextGeneration ? "�����" : "����");
            }
        });
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(fillButton);
        btnPanel.add(stepButton);
        btnPanel.add(goButton);
        btnPanel.add(curGenerationLabel);
 
        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, btnPanel);
        frame.setVisible(true);

        while (true) {
            if (goNextGeneration) {
                processOfLife();
                canvasPanel.repaint();
                try {
                    Thread.sleep(showDelay);
                } catch (InterruptedException e) { 
                	e.printStackTrace(); 
                }
            }
        }
	}
 
    //���������� �������
	private static int countNeighbors(int x, int y) {
        int count = 0;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                int nX = x + dx;
                int nY = y + dy;
                nX = (nX < 0) ? LIFE_SIZE - 1 : nX;
                nY = (nY < 0) ? LIFE_SIZE - 1 : nY;
                nX = (nX > LIFE_SIZE - 1) ? 0 : nX;
                nY = (nY > LIFE_SIZE - 1) ? 0 : nY;
                count += (lifeGeneration[nX][nY]) ? 1 : 0;
            }
        }
        if (lifeGeneration[x][y]) { 
        	--count; 
        }
        return count;
    }
	
	private static boolean endOfGame() {
		boolean ended = false, stat = false, cycle = false;
        for (int x = 0; x < LIFE_SIZE; x++) {
           if (Arrays.equals(lifeGeneration[x], new boolean[LIFE_SIZE])) {
        	   ended = true;
           } else {
        	   ended = false;
        	   break;
           }
        }
        for (int x = 0; x < LIFE_SIZE; x++) {
           if (Arrays.equals(nextGeneration[x], prevGeneration[x])) {
        	   stat = true;
           } else {
        	   stat = false;
        	   break;
           }
        }
        for (int x = 0; x < LIFE_SIZE; x++) {
            if (Arrays.equals(nextGeneration[x], lifeGeneration[x])) {
         	   cycle = true;
            } else {
         	   cycle = false;
         	   break;
            }
         }
        if (ended || stat || cycle) {
        	JFrame framix = new JFrame("Sorry");
        	JOptionPane.showMessageDialog(framix, "���� ���������. " + 
        			(ended ? "��� ������ ������." : "��������� ���������� ������������."));
        	goNextGeneration = !goNextGeneration;
        	return true;
        }
        return false;
	}
	
	private static void processOfLife() {
        for (int x = 0; x < LIFE_SIZE; x++) {
            for (int y = 0; y < LIFE_SIZE; y++) {
                int count = countNeighbors(x, y);
                nextGeneration[x][y] = lifeGeneration[x][y];
                //���� ��� ������ ����� ���� - ������ ���������
                nextGeneration[x][y] = (count == 3) 
                						? true 
                						: nextGeneration[x][y];
                //���� ������ ���� ��� ������ ���� ����� ���� - ������ �������
                nextGeneration[x][y] = ((count < 2) || (count > 3)) 
                						? false 
                						: nextGeneration[x][y];
            }
        }
        if (!endOfGame()) {
        	for (int x = 0; x < LIFE_SIZE; x++) {
        		System.arraycopy(lifeGeneration[x], 0, prevGeneration[x], 0, LIFE_SIZE);
        	}
        	for (int x = 0; x < LIFE_SIZE; x++) {
        		System.arraycopy(nextGeneration[x], 0, lifeGeneration[x], 0, LIFE_SIZE);
        	}

        	++currentGeneration;
        	curGenerationLabel.setText("������� ���������: " 
        								+ currentGeneration);
        }
    }
}