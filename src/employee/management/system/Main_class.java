package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main_class extends JFrame{

    Main_class(){


        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icons/home.jpg"));
        Image i2 =  i1.getImage().getScaledInstance(1120,650,Image.SCALE_DEFAULT);
        ImageIcon i3 = new ImageIcon(i2);
        JLabel image = new JLabel(i3);
        image.setBounds(0,0,1120,630);
        add(image);

        JLabel heading = new JLabel("Employee Management System");
        heading.setBounds(370,140,400,40);
        heading.setFont(new Font("Times New Roman",Font.BOLD,25));
        image.add(heading);

        JButton add = new JButton("Add Employee");
        add.setBounds(355, 270, 150, 40);
        add.setForeground(Color.white);
        add.setBackground(Color.black);
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddEmployee().setVisible(true);

            }
        });
        image.add(add);

        JButton view = new JButton("View Employee");
        view.setBounds(565, 270, 150, 40);
        view.setForeground(Color.white);
        view.setBackground(Color.black);
        view.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ViewEmployee().setVisible(true);

            }
        });
        image.add(view);

        JButton rem = new JButton("Remove Employee");
        rem.setBounds(440, 370, 150, 40);
        rem.setForeground(Color.white);
        rem.setBackground(Color.black);
        rem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RemoveEmployee().setVisible(true);
            }
        });
        image.add(rem);

        setSize(1120,650);
        setLocation(250,100);
        setTitle("Home");
        setLayout(null);
        setVisible(true);

    }

    public static void main(){
        new Main_class();

    }
}
