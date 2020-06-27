package agents;

import verdantium.clmgr.ClasspathManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import core.AgentManager;
import core.SongData;


public class Activator implements BundleActivator {
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		System.out.println( "Starting Agent Add..." );
		
		ClasspathManager.addClass( HighFreqGuitarPlayingAgent.class );
		
		AgentManager.addClass( HighFreqGuitarPlayingAgent.class , "High Guitar Agent");

		AgentManager.addClass( LowFreqGuitarPlayingAgent.class , "Low Guitar Agent" );
		
		AgentManager.addClass( PercussiveAgentGuitarLike.class , "Percussive Agent Guitar-Like" );

		AgentManager.addClass( BassGuitarPlayingAgent.class , "Bass Guitar Agent" );

		AgentManager.addClass( BassPercussionAgent.class , "Bass Percussion Agent" );

		AgentManager.addClass( ScratchPercussionAgent.class , "Scratch Percussion Agent" );

		AgentManager.addClass( SnarePercussionAgent.class , "Snare Percussion Agent" );
		
		AgentManager.addClass( SnarePercussionAgentSky.class , "Snare Percussion Agent Sky" );
		
		AgentManager.addClass( MetalPlatePercussionAgent.class , "Metal-Plate Percussion Agent" );

		AgentManager.addClass( SampledAgent.class , "Sampled Agent" );

		AgentManager.addClass( SquirerAgentS1.class , "Squirer S1 Agent" );

		AgentManager.addClass( BassShackGuitarAgent.class , "Bass Shack Guitar Agent" );

		AgentManager.addClass( BassCrystDrumAgent.class , "Bass Cryst Drum Agent" );

		AgentManager.addClass( DeepLowStringAgent.class , "Deep Low String Agent" );
		
		AgentManager.addClass( XSquirerAgentS3.class , "X Squirer S3 Agent" );
		
		AgentManager.addClass( PseudoEndoVibeAgent.class , "Pseudo endo Vibe Agent" );
		
		AgentManager.addClass( BassEAgent.class , "Bass E Agent" );
		
		AgentManager.addClass( BassLoAgentMohe.class , "Bass Lo Agent Mohe" );
		
		AgentManager.addClass( BassLoAgentMoheNoDistort.class, "Bass Lo Agent Mohe No Distort" );
		
		AgentManager.addClass( MidAgentMohe.class , "Mid Agent Mohe" );
		
		AgentManager.addClass( MidAgentMoheNoDistort.class , "Mid Agent Mohe No Distort" );
		
		AgentManager.addClass( WhistleAgent.class , "Whistle Agent" );
		
		AgentManager.addClass( WhistleBAgent.class , "Whistle B Agent" );
		
		AgentManager.addClass( ExprAgentS6.class , "Expr Agent S6" );
		
		AgentManager.addClass( ExprAgentS5.class , "Expr Agent S5" );
		
		AgentManager.addClass( ExprAgentS4.class , "Expr Agent S4" );
		
		AgentManager.addClass( ExprAgentS3.class , "Expr Agent S3" );
		
		AgentManager.addClass( ExprAgentS2.class , "Expr Agent S2" );
		
		AgentManager.addClass( ExprAgentS1.class , "Expr Agent S1" );
		
		AgentManager.addClass( ZSoundAgentS3.class , "ZSound S3 Agent" );
		
		AgentManager.addClass( ZSoundAgentS2.class , "ZSound S2 Agent" );
		
		AgentManager.addClass( ZSoundAgentS1Dry.class , "ZSound S1 Dry Agent" );
		
		AgentManager.addClass( BassLoExprAgentS0.class , "Bass Lo Expr Agent S0" );
		
		AgentManager.addClass( BassLoExprAgentS1.class , "Bass Lo Expr Agent S1" );
		
		AgentManager.addClass( BassLoExprAgentS2.class , "Bass Lo Expr Agent S2" );
		
		AgentManager.addClass( BassLoExprAgentS3.class , "Bass Lo Expr Agent S3" );
		
		AgentManager.addClass( BassLoExprAgentS4.class , "Bass Lo Expr Agent S4" );
		
		AgentManager.addClass( NativeFluteAgentP0.class , "Native Flute Agent P0" );
		
		AgentManager.addClass( MultifractalBassLoGuitarAgentS_neg1.class , "Multifractal Bass Lo Guitar Agent S_neg1" );
		
		AgentManager.addClass( MultifractalBassLoGuitarAgentS0.class , "Multifractal Bass Lo Guitar Agent S0" );
		
		AgentManager.addClass( MultifractalBassLoGuitarAgentS1.class , "Multifractal Bass Lo Guitar Agent S1" );
		
		AgentManager.addClass( MultifractalGuitarAgentS6.class , "Multifractal Guitar Agent S6" );
		
		AgentManager.addClass( MultifractalGuitarAgentS5.class , "Multifractal Guitar Agent S5" );
		
		AgentManager.addClass( MultifractalGuitarAgentS4.class , "Multifractal Guitar Agent S4" );
		
		AgentManager.addClass( FractalSawtoothAgent.class , "Fractal Sawtooth Agent" );
		
		AgentManager.addClass( SimplisticSquareWaveAgent.class , "Simplistic Square-Wave Agent" );
		
		AgentManager.addClass( MultifractalMetallicInstrumentAgent.class , "Multifractal Metallic Instrument Agent" );
		
		AgentManager.addClass( MultifractalSynthPadHighAgent.class , "Multifractal Synth Pad High Agent" );
		
		
		int core = 0;
		try
		{
			SongData.handleWaveChange(core);
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		System.out.println( "Ending Agent Add..." );
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
		System.out.println( "Starting Agent Remove..." );
		
		ClasspathManager.removeClass( HighFreqGuitarPlayingAgent.class );
		
		AgentManager.removeClass( HighFreqGuitarPlayingAgent.class , "High Guitar Agent");

		AgentManager.removeClass( LowFreqGuitarPlayingAgent.class , "Low Guitar Agent" );
		
		AgentManager.removeClass( PercussiveAgentGuitarLike.class , "Percussive Agent Guitar-Like" );

		AgentManager.removeClass( BassGuitarPlayingAgent.class , "Bass Guitar Agent" );

		AgentManager.removeClass( BassPercussionAgent.class , "Bass Percussion Agent" );

		AgentManager.removeClass( ScratchPercussionAgent.class , "Scratch Percussion Agent" );

		AgentManager.removeClass( SnarePercussionAgent.class , "Snare Percussion Agent" );

		AgentManager.removeClass( SampledAgent.class , "Sampled Agent" );

		AgentManager.removeClass( SquirerAgentS1.class , "Squirer S1 Agent" );

		AgentManager.removeClass( BassShackGuitarAgent.class , "Bass Shack Guitar Agent" );

		AgentManager.removeClass( BassCrystDrumAgent.class , "Bass Cryst Drum Agent" );

		AgentManager.removeClass( DeepLowStringAgent.class , "Deep Low String Agent" );
		
		AgentManager.removeClass( XSquirerAgentS3.class , "X Squirer S3 Agent" );
		
		AgentManager.removeClass( PseudoEndoVibeAgent.class , "Pseudo endo Vibe Agent" );
		
		AgentManager.removeClass( BassEAgent.class , "Bass E Agent" );
		
		AgentManager.removeClass( BassLoAgentMohe.class , "Bass Lo Agent Mohe" );
		
		AgentManager.removeClass( BassLoAgentMoheNoDistort.class, "Bass Lo Agent Mohe No Distort" );
		
		AgentManager.removeClass( MidAgentMohe.class , "Mid Agent Mohe" );
		
		AgentManager.removeClass( MidAgentMoheNoDistort.class , "Mid Agent Mohe No Distort" );
		
		AgentManager.removeClass( WhistleAgent.class , "Whistle Agent" );
		
		AgentManager.removeClass( WhistleBAgent.class , "Whistle B Agent" );
		
		AgentManager.removeClass( ExprAgentS6.class , "Expr Agent S6" );
		
		AgentManager.removeClass( ExprAgentS5.class , "Expr Agent S5" );
		
		AgentManager.removeClass( ExprAgentS4.class , "Expr Agent S4" );
		
		AgentManager.removeClass( ExprAgentS3.class , "Expr Agent S3" );
		
		AgentManager.removeClass( ExprAgentS2.class , "Expr Agent S2" );
		
		AgentManager.removeClass( ExprAgentS1.class , "Expr Agent S1" );
		
		AgentManager.removeClass( ZSoundAgentS3.class , "ZSound S3 Agent" );
		
		AgentManager.removeClass( ZSoundAgentS2.class , "ZSound S2 Agent" );
		
		AgentManager.removeClass( ZSoundAgentS1Dry.class , "ZSound S1 Dry Agent" );
		
		AgentManager.removeClass( BassLoExprAgentS0.class , "Bass Lo Expr Agent S0" );
		
		AgentManager.removeClass( BassLoExprAgentS1.class , "Bass Lo Expr Agent S1" );
		
		AgentManager.removeClass( BassLoExprAgentS2.class , "Bass Lo Expr Agent S2" );
		
		AgentManager.removeClass( BassLoExprAgentS3.class , "Bass Lo Expr Agent S3" );
		
		AgentManager.removeClass( BassLoExprAgentS4.class , "Bass Lo Expr Agent S4" );
		
		AgentManager.removeClass( NativeFluteAgentP0.class , "Native Flute Agent P0" );
		
		
		int core = 0;
		try
		{
			SongData.handleWaveChange(core);
		}
		catch( Throwable ex )
		{
			ex.printStackTrace( System.out );
		}
		System.out.println( "Ending Agent Remove..." );
	}


}

