package com.sample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class RuleSystem {

	public static RuleSystem INSTANCE;
	
	@FunctionalInterface
	public interface OptionCallback {
		void callback();
	}
	
	private class Option {
		
		public Option(JRadioButton button, OptionCallback onSelected) {
			this.button = button;
			this.onSelected = onSelected;
		}
		
		public JRadioButton button;
		public OptionCallback onSelected;
	
	}
	
	private JTextArea question;
	
	private JPanel optionsPanel;
	private ButtonGroup optionsGroup;
	
	private List<Option> options;
	
	private KieSession ksess;
	
	public static final void main(String[] args) {
        // KieServices is the factory for all KIE services
        KieServices ks = KieServices.Factory.get();

        // From the kie services, a container is created from the classpath
        KieContainer kc = ks.getKieClasspathContainer();

        RuleSystem.INSTANCE = new RuleSystem();
        RuleSystem.INSTANCE.init();
        
        RuleSystem.INSTANCE.ksess = kc.newKieSession("PetStoreKS");
//    	KieRuntimeLogger kLogger = ks.getLoggers().newFileLogger(RuleSystem.INSTANCE.ksess, "test");
        
    	RuleSystem.INSTANCE.ksess.fireAllRules();
//        kLogger.close();
    }
	
	public void init() {
		//Create the Jframe
		JFrame f = new JFrame("Rule System");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Create the JPanel
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		
		question = new JTextArea();
		question.setEditable(false);
		question.setText("Loading...");
		panel.add(question);
		
		optionsPanel = new JPanel();
		panel.add(optionsPanel);
		
		optionsGroup = new ButtonGroup();
		
		options = new ArrayList<Option>();
		
		//Create button 2
		JButton btn2 = new JButton("Submit"); 
		btn2.setBounds(100, 100, 80, 30);  
		btn2.addMouseListener(new SubmitHandler());
		panel.add(btn2);
		
		f.setLayout(new BorderLayout());
		f.add(panel, BorderLayout.CENTER);
		f.pack();
		f.setVisible(true);
		f.setMinimumSize(new Dimension(500, 400));
	}
	
	public void setQuestion(String text) {
		System.out.println("<- " + text);
		
		question.setText(text);
		clearOptions();
	}
	
	public void addOption(String name, OptionCallback onSelected) {
		JRadioButton opt = new JRadioButton(name);
		opt.setActionCommand(""+options.size());
		
		optionsPanel.add(opt);
		optionsGroup.add(opt);
		
		if (options.size() == 0) {
			optionsGroup.setSelected(opt.getModel(), true);
		}
		
		options.add(new Option(opt, onSelected));
		
		optionsPanel.revalidate();
		optionsPanel.repaint();
	}
	
	public void clearOptions() {
		for (Option opt : options) {
			optionsPanel.remove(opt.button);
			optionsGroup.remove(opt.button);
		}
		
		options.clear();
		
		optionsPanel.revalidate();
		optionsPanel.repaint();
	}
	
	private class SubmitHandler extends MouseAdapter {
		public void mouseReleased(MouseEvent e) {
			if (options.isEmpty()) {
				return;
			}
			
			String cmd = optionsGroup.getSelection().getActionCommand();
			Option opt = options.get(Integer.parseInt(cmd));
			
			System.out.println("-> " + opt.button.getText());
			
			opt.onSelected.callback();
			
			ksess.fireAllRules();
        }
	}
	
}
