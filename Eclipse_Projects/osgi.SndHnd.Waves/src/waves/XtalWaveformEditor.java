





//$$strtCprt
/**
* SndHnd
* 
* Copyright (C) 1992-2020 Thornton Green
* 
* This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
* published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
* of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
* You should have received a copy of the GNU General Public License along with this program; if not, 
* see <http://www.gnu.org/licenses>.
* Additional permission under GNU GPL version 3 section 7
*
*/
//$$endCprt







package waves;



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import verdantium.VerdantiumUtils;
import verdantium.utils.IllegalInputException;
import verdantium.utils.VerticalLayout;
import verdantium.xapp.PropertyEditAdapter;
import core.InstrumentTrack;



/**
* A property editor that allows the size of the document page to be changed for
* a client property editor or component.
* <P>
* @author Thorn Green
*/
public class XtalWaveformEditor
	extends PropertyEditAdapter
	implements ActionListener {
	
	/**
	* The panel in which the property editor lies.
	*/
	private JPanel MyPan = new JPanel();
	
	protected GXtalWaveform waveForm = null;
	
	private JTextField offTime = new JTextField();
	
	private JTextField freq = new JTextField();
	
	private JTextField mx = new JTextField();
	private JTextField my = new JTextField();
	
	private JTextField m1 = new JTextField();
	private JTextField m2 = new JTextField();
	private JTextField m3 = new JTextField();
	
	private JTextField nx = new JTextField();
	private JTextField ny = new JTextField();
	
	private JTextField n1 = new JTextField();
	private JTextField n2 = new JTextField();
	private JTextField n3 = new JTextField();
	
	protected InstrumentTrack ins;
	

	
	/**
	* Constructs the property editor for a given PageSizeHandler.
	*/
	public XtalWaveformEditor(GXtalWaveform in , InstrumentTrack _ins) {
		waveForm = in;
		ins = _ins;
		MyPan.setLayout(new BorderLayout(0, 0));
		JButton ApplyButton = new JButton("Apply");
		MyPan.add(BorderLayout.SOUTH, ApplyButton);

		JPanel pan2 = new JPanel();
		MyPan.add(BorderLayout.CENTER, pan2);
		pan2.setLayout(new VerticalLayout(1));
		
		pan2.add("any", new JLabel("Off Time : "));
		pan2.add("any", offTime);
		
		pan2.add("any", new JLabel("Freq : "));
		pan2.add("any", freq);
		
		pan2.add("any", new JLabel("Mx : "));
		pan2.add("any", mx);
		
		pan2.add("any", new JLabel("My : "));
		pan2.add("any", my);
		
		pan2.add("any", new JLabel("M1 : "));
		pan2.add("any", m1);
		
		pan2.add("any", new JLabel("M2 : "));
		pan2.add("any", m2);
		
		pan2.add("any", new JLabel("M3 : "));
		pan2.add("any", m3);
		
		pan2.add("any", new JLabel("Nx : "));
		pan2.add("any", nx);
		
		pan2.add("any", new JLabel("Ny : "));
		pan2.add("any", ny);
		
		pan2.add("any", new JLabel("N1 : "));
		pan2.add("any", n1);
		
		pan2.add("any", new JLabel("N2 : "));
		pan2.add("any", n2);
		
		pan2.add("any", new JLabel("N3 : "));
		pan2.add("any", n3);
		
		
		
		offTime.setText(""+in.getOffTime());
		
		freq.setText(""+in.getFreq());
		
		mx.setText(""+in.getMx());
		
		my.setText(""+in.getMy());
		
		m1.setText(""+in.getM1());
		
		m2.setText(""+in.getM2());
		
		m3.setText(""+in.getM3());
		
		nx.setText(""+in.getNx());
		
		ny.setText(""+in.getNy());
		
		n1.setText(""+in.getN1());
		
		n2.setText(""+in.getN2());
		
		n3.setText(""+in.getN3());

		
		ApplyButton.addActionListener(this);
	}

	/**
	* Gets the GUI of the property editor.
	*/
	public JComponent getGUI() {
		return (MyPan);
	}

	/**
	* Handles the destruction of the component by removing appropriate change listeners.
	*/
	public void handleDestroy() {
	}

	/**
	* Handles a button-press event from the Apply button by changing
	* the size of the client page size handler.
	*/
	public void actionPerformed(ActionEvent e) {
		try {
			final int core = 0;
			
			
			waveForm.setOffTime(Double.parseDouble(offTime.getText()));
			
			waveForm.setFreq(Double.parseDouble(freq.getText()));
			
			waveForm.setMx(Double.parseDouble(mx.getText()));
			
			waveForm.setMy(Double.parseDouble(my.getText()));
			
			waveForm.setM1(Double.parseDouble(m1.getText()));
			
			waveForm.setM2(Double.parseDouble(m2.getText()));
			
			waveForm.setM3(Double.parseDouble(m3.getText()));
			
			waveForm.setNx(Double.parseDouble(nx.getText()));
			
			waveForm.setNy(Double.parseDouble(ny.getText()));
			
			waveForm.setN1(Double.parseDouble(n1.getText()));
			
			waveForm.setN2(Double.parseDouble(n2.getText()));
			
			waveForm.setN3(Double.parseDouble(n3.getText()));
			
			
			ins.updateTrackFrames(core);

			
		//	EtherEvent send =
		//		new PropertyEditEtherEvent(
		//			this,
		//			PropertyEditEtherEvent.setPageSize,
		//			null,
		//			MyPage);
		//	send.setParameter(new Dimension((int) wid, (int) hei));
		//	ProgramDirector.fireEtherEvent(send, null);
		} catch (NumberFormatException ex) {
			handleThrow(
				new IllegalInputException(
					"Something input was not a number.",
					ex));
		} catch (Throwable ex) {
			handleThrow(ex);
		}
	}

	/**
	* Handles the throwing of an error or exception.
	*/
	public void handleThrow(Throwable in) {
		VerdantiumUtils.handleThrow(in, this, null);
	}

}

