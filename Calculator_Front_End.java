import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Calculator_Front_End extends JFrame{

	/**
	 * 	 
	 *  
	 */
	private static final long serialVersionUID = 8263923089039447963L;

	Calculator backEnd;
	JTextField expression;
	varPanel varP;
	JLabel curExpression = new JLabel("Please Enter an Expression Above First");
	JFrame parent;

	public Calculator_Front_End(){
		super("Sig-Fig Calculator");
		
		JPanel temp = new JPanel();
		temp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		backEnd = new Calculator();
		parent = this;

		setName("Sig-Fig Calculator");
		temp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5,5,5,5);

		gbc.gridy = 0;
		expression = new JTextField(40);
		expression.setBorder(BorderFactory.createEtchedBorder());
		temp.add(expression,gbc);

		gbc.gridy++;
		temp.add(curExpression,gbc);
		curExpression.setFont(curExpression.getFont().deriveFont((float) 17.0));
		
		
		gbc.gridy++;
		varP = new varPanel();
		temp.add(varP,gbc);

		gbc.gridy++;
		temp.add(new buttonPanel(),gbc);

		this.add(temp);
		this.setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	class varPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1067247239538084675L;
		ArrayList<JTextField> values;
		ArrayList<JTextField> uncertainties;
		ArrayList<JCheckBox> isCertain;

		public varPanel(){
			super();
			setLayout(new GridBagLayout());
			//JLabel label = new JLabel("Please Enter an Expression Above First");
			//GridBagConstraints gbc = new GridBagConstraints();
			//add(label, gbc);

		}

		public void setupvars(Set<String> varNames){

			removeAll();
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.NONE;
			gbc.insets = new Insets(0,5,0,5);
			gbc.gridx = 1;
			add(new JLabel("Value"),gbc);
			gbc.gridx++;
			add(new JLabel("Uncertainty"),gbc);
			gbc.gridx++;
			add(new JLabel("Is A Constant"),gbc);

			values = new ArrayList<JTextField>();
			uncertainties = new ArrayList<JTextField>();
			isCertain = new ArrayList<JCheckBox>();
			for(String var: varNames){

				gbc.gridy++;
				gbc.gridx = 0;
				add(new JLabel(var),gbc);

				gbc.gridx++;
				JTextField val = new JTextField(15);
				values.add(val);
				add(val, gbc);

				gbc.gridx++;
				JTextField unc = new JTextField(15);
				uncertainties.add(unc);
				add(unc, gbc);

				gbc.gridx++;
				JCheckBox box = new JCheckBox();
				int x = isCertain.size();
				box.addActionListener(new ActionListener(){

					public void actionPerformed(ActionEvent e) {

						uncertainties.get(x).setEnabled(!box.isSelected());
						uncertainties.get(x).setText("");
					}

				});
				isCertain.add(box);
				add(box,gbc);

			}	
		}
	}

	class buttonPanel extends JPanel{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8901114616358441887L;
		JButton loadExpr;
		JButton calculate;
		JButton clearAll;

		public buttonPanel(){

			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();



			loadExpr = new JButton("Load Expression");
			gbc.anchor = GridBagConstraints.WEST;
			gbc.gridx = 0;
			loadExpr.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent event) {
					if(!expression.getText().equals("")){
						System.out.println(expression.getText());
						try{
							backEnd.setFunction(expression.getText());
							varP.setupvars(backEnd.getVariables());
							curExpression.setText(expression.getText());
							calculate.setEnabled(true);
							parent.pack();
						}
						catch(Exception e){
							JOptionPane.showMessageDialog(null,"Error While Parsing: Please Check that your function input");
						}
					}
				}

			});
			add(loadExpr);

			calculate = new JButton("Calculate");
			gbc.anchor = GridBagConstraints.CENTER;
			gbc.gridx = 1;
			add(calculate);
			calculate.setEnabled(false);
			calculate.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {

					try{
						Map<String, Value> variables = new HashMap<String,Value>();

						int i = 0;
						for(String var: backEnd.getVariables()){

							double v = Double.parseDouble(varP.values.get(i).getText());
							double u = (varP.isCertain.get(i).isSelected()?0:Double.parseDouble(varP.uncertainties.get(i).getText()));
							variables.put(var, new Value(v,u));
							i++;

						}
						backEnd.setVars(variables);
						Value ret = backEnd.calculate();
						JOptionPane.showMessageDialog(parent, "Result: " + ret);
					}catch(Exception exception){
						JOptionPane.showMessageDialog(null, "Malicious input detected please check input variables");
					}

				}

			});
			calculate.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub

				}

			});


			clearAll = new JButton("Clear All");
			gbc.anchor = GridBagConstraints.EAST;
			gbc.gridx = 2;
			add(clearAll);
			clearAll.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {

					varP.removeAll();
					//varP.add(new JLabel("Please Enter an Expression Above First"));
					curExpression.setText("Please Enter an Expression Above First");
					curExpression.setFont(curExpression.getFont().deriveFont(18));
					expression.setText("");
					parent.pack();
				}

			});


		}

	}
}
