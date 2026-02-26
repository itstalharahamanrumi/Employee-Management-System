package employee.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;

public class UpdateEmployee extends JFrame implements ActionListener {

    JTextField teducation, tfname, taddress, tphone, temail, tsalary, tdesignation;
    JLabel tempid, tname, tdob;
    JButton update, back;
    String number;

    UpdateEmployee(String number) {
        this.number = number;

        getContentPane().setBackground(new Color(230, 255, 188));
        setLayout(null);

        JLabel heading = new JLabel("Update Employee Details");
        heading.setForeground(Color.black);
        heading.setBounds(320, 30, 500, 50);
        heading.setFont(new Font("SERIF", Font.BOLD, 25));
        add(heading);

        JLabel name = new JLabel("Name:");
        name.setBounds(50, 150, 150, 30);
        name.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(name);

        tname = new JLabel();
        tname.setBounds(200, 150, 150, 30);
        tname.setFont(new Font("SAN_SERIF", Font.PLAIN, 18));
        add(tname);

        JLabel fname = new JLabel("Father's Name:");
        fname.setBounds(400, 150, 150, 30);
        fname.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(fname);

        tfname = new JTextField();
        tfname.setBounds(600, 150, 150, 30);
        tfname.setBackground(new Color(230, 255, 188));
        add(tfname);

        JLabel dob = new JLabel("Date of Birth:");
        dob.setBounds(50, 200, 150, 30);
        dob.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(dob);

        tdob = new JLabel();
        tdob.setBounds(200, 200, 150, 30);
        tdob.setFont(new Font("Tahoma", Font.BOLD, 18));
        add(tdob);

        JLabel salary = new JLabel("Salary:");
        salary.setBounds(400, 200, 150, 30);
        salary.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(salary);

        tsalary = new JTextField();
        tsalary.setBounds(600, 200, 150, 30);
        tsalary.setBackground(new Color(230, 255, 188));
        add(tsalary);

        JLabel address = new JLabel("Address:");
        address.setBounds(50, 250, 150, 30);
        address.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(address);

        taddress = new JTextField();
        taddress.setBounds(200, 250, 150, 30);
        taddress.setBackground(new Color(230, 255, 188));
        add(taddress);

        JLabel phone = new JLabel("Phone:");
        phone.setBounds(400, 250, 150, 30);
        phone.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(phone);

        tphone = new JTextField();
        tphone.setBounds(600, 250, 150, 30);
        tphone.setBackground(new Color(230, 255, 188));
        add(tphone);

        JLabel email = new JLabel("Email:");
        email.setBounds(50, 300, 150, 30);
        email.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(email);

        temail = new JTextField();
        temail.setBounds(200, 300, 150, 30);
        temail.setBackground(new Color(230, 255, 188));
        add(temail);

        JLabel education = new JLabel("Educational Qualification:");
        education.setBounds(400, 300, 220, 30);
        education.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
        add(education);

        teducation = new JTextField();
        teducation.setBounds(630, 300, 150, 30);
        teducation.setBackground(new Color(230, 255, 188));
        add(teducation);

        JLabel designation = new JLabel("Designation:");
        designation.setBounds(50, 350, 150, 30);
        designation.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(designation);

        tdesignation = new JTextField();
        tdesignation.setBounds(200, 350, 150, 30);
        tdesignation.setBackground(new Color(230, 255, 188));
        add(tdesignation);

        JLabel empid = new JLabel("Employee ID:");
        empid.setBounds(400, 350, 150, 30);
        empid.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        add(empid);

        tempid = new JLabel();
        tempid.setBounds(600, 350, 150, 30);
        tempid.setFont(new Font("SAN_SERIF", Font.BOLD, 20));
        tempid.setForeground(Color.red);
        add(tempid);


        try {
            Conn c = new Conn();
            String query = "select * from employee where empid = '" + number + "'";
            ResultSet rs = c.statement.executeQuery(query);
            while (rs.next()) {
                tname.setText(rs.getString("name"));
                tfname.setText(rs.getString("fname"));
                tdob.setText(rs.getString("dob"));
                taddress.setText(rs.getString("address"));
                tsalary.setText(rs.getString("salary"));
                tphone.setText(rs.getString("phone"));
                temail.setText(rs.getString("email"));
                teducation.setText(rs.getString("education"));
                tdesignation.setText(rs.getString("designation"));
                tempid.setText(rs.getString("empid"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        update = new JButton("UPDATE");
        update.setBounds(450, 550, 150, 40);
        update.setBackground(Color.black);
        update.setForeground(Color.white);
        update.addActionListener(this);
        add(update);

        back = new JButton("BACK");
        back.setBounds(250, 550, 150, 40);
        back.setBackground(Color.black);
        back.setForeground(Color.white);
        back.addActionListener(this);
        add(back);

        setTitle("Update Employee");
        setSize(900, 700);
        setLocation(300, 50);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == update) {
            String fname = tfname.getText();
            String salary = tsalary.getText();
            String address = taddress.getText();
            String phone = tphone.getText();
            String email = temail.getText();
            String education = teducation.getText();
            String designation = tdesignation.getText();

            try {
                Conn c = new Conn();
                String query = "update employee set fname='" + fname +
                        "', salary='" + salary +
                        "', address='" + address +
                        "', phone='" + phone +
                        "', email='" + email +
                        "', education='" + education +
                        "', designation='" + designation +
                        "' where empid='" + number + "'";
                c.statement.executeUpdate(query);
                JOptionPane.showMessageDialog(null, "Employee Details Updated Successfully");
                setVisible(false);
                new Main_class();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            setVisible(false);
            new ViewEmployee();
        }
    }

    public static void main(String[] args) {
        new UpdateEmployee("");
    }
}
