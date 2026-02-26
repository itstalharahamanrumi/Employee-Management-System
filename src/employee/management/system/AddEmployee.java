package employee.management.system;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class AddEmployee extends JFrame implements ActionListener {
    Random rand=new Random();
    int number = rand.nextInt(999999);

    JTextField tname, tfname, taddress, tphone, temail, tsalary, tdesignation;

    JLabel tempid;

    JButton add, back;


    JDateChooser tdob;

    JComboBox Boxeducation;


    AddEmployee(){


        getContentPane().setBackground(new Color(230,255,188));

        JLabel heading = new JLabel("Add Employee Details");
        heading.setForeground(Color.black);
        heading.setBounds(320,30,500,50);
        heading.setFont(new Font("SERIF",Font.BOLD,25));
        add(heading);


        JLabel name = new JLabel("Name:");
        name.setBounds(50,150,150,30);
        name.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(name);

        tname = new JTextField();
        tname.setBounds(200,150,150,30);
        tname.setBackground(new Color(230,255,188));
        add(tname);

        JLabel fname = new JLabel("Father's Name:");
        fname.setBounds(400,150,150,30);
        fname.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(fname);

        tfname = new JTextField();
        tfname.setBounds(600,150,150,30);
        tfname.setBackground(new Color(230,255,188));
        add(tfname);

        JLabel dob = new JLabel("Date of Birth:");
        dob.setBounds(50,200,150,30);
        dob.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(dob);

        tdob = new JDateChooser();
        tdob.setBounds(200,200,150,30);
        tdob.setBackground(new Color(230,255,188));
        add(tdob);


        JLabel salary = new JLabel("Salary:");
        salary.setBounds(400,200,150,30);
        salary.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(salary);

        tsalary = new JTextField();
        tsalary.setBounds(600,200,150,30);
        tsalary.setBackground(new Color(230,255,188));
        add(tsalary);

        JLabel address = new JLabel("Address:");
        address.setBounds(50,250,150,30);
        address.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(address);

        taddress = new JTextField();
        taddress.setBounds(200,250,150,30);
        taddress.setBackground(new Color(230,255,188));
        add(taddress);

        JLabel phone = new JLabel("Phone:");
        phone.setBounds(400,250,150,30);
        phone.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(phone);

        tphone = new JTextField();
        tphone.setBounds(600,250,150,30);
        tphone.setBackground(new Color(230,255,188));
        add(tphone);

        JLabel email = new JLabel("Email:");
        email.setBounds(50,300,150,30);
        email.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(email);

        temail = new JTextField();
        temail.setBounds(200,300,150,30);
        temail.setBackground(new Color(230,255,188));
        add(temail);

        JLabel education = new JLabel("Eduational Qualification:");
        education.setBounds(400,300,150,30);
        education.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(education);

        String items[] = {" ","CSE", "BBA", "LLB", "CE", "TE", "ME", "EEE", "ENGLISH"};
        Boxeducation = new JComboBox(items);
        Boxeducation.setBackground(new Color(230,255,188));
        Boxeducation.setBounds(600,300,150,30);
        add(Boxeducation);

        JLabel designation = new JLabel("Designation:");
        designation.setBounds(50,350,150,30);
        designation.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(designation);

        tdesignation = new JTextField();
        tdesignation.setBounds(200,350,150,30);
        tdesignation.setBackground(new Color(230,255,188));
        add(tdesignation);

        JLabel empid = new JLabel("Employee ID:");
        empid.setBounds(400,350,150,30);
        empid.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        add(empid);

        tempid = new JLabel(""+number);
        tempid.setBounds(600,350,150,30);
        tempid.setFont(new Font("SAN_SERIF",Font.BOLD,20));
        tempid.setForeground(Color.red);
        add(tempid);


        add = new JButton("ADD");
        add.setBounds(450,550,150,40);
        add.setBackground(Color.black);
        add.setForeground(Color.white);
        add.addActionListener(this);
        add(add);

        back = new JButton("BACK");
        back.setBounds(250,550,150,40);
        back.setBackground(Color.black);
        back.setForeground(Color.white);
        back.addActionListener(this);
        add(back);


        setTitle("Add Employee");
        setSize(900,700);
        setLayout(null);
        setLocation(300,50);
        setVisible(true);


    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource()==add){
            String name = tname.getText();
            String fname = tfname.getText();
            String address = taddress.getText();
            String phone = tphone.getText();
            String email = temail.getText();
            String education = (String) Boxeducation.getSelectedItem();
            String dob = ((JTextField) tdob.getDateEditor().getUiComponent()).getText();
            String salary = tsalary.getText();
            String designation = tdesignation.getText();
            String empid = tempid.getText();

            try{
                Conn c = new Conn();
                String query = "insert into employee values('"+name+"', '"+fname+"', '"+address+"', '"+phone+"', '"+email+"','"+education+"', '"+dob+"', '"+salary+"','"+designation+"', '"+empid+"')";
                c.statement.execute(query);
                JOptionPane.showMessageDialog(null, "Employee Added Successfully");
                setVisible(false);
                new Main_class();

            }catch (Exception E){
                E.printStackTrace();

            }
        } else {
            setVisible(false);
            new Main_class();
        }
    }

    public static void main(String[] args) {
        new AddEmployee();

    }
}
