package agents;
import gredit.GWaveForm;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import meta.DataFormatException;
import meta.VersionBuffer;
import waves.ArithGntWaveform;
import waves.GAppxBezWaveform;
import waves.GRoughDraftWaveSwitch;
import waves.MultiXtalNativeFluteP0;
import aazon.builderNode.AazonTransChld;
import aczon.AczonUnivAllocator;
import bezier.BezierCubicClampedCoefficient;
import bezier.BezierCubicNonClampedCoefficientFlat;
import bezier.PiecewiseCubicMonotoneBezierFlat;
import bezier.PiecewiseCubicMonotoneBezierFlatMultiCore;
import core.ClampedCoefficient;
import core.FreqAndBend;
import core.InstrumentTrack;
import core.IntelligentAgent;
import core.InterpolationPoint;
import core.NonClampedCoefficient;
import core.NoteDesc;
import core.NoteInitializer;
import core.NoteTable;
import core.PhaseDistortionPacket;
import core.SongData;
import core.TrackFrame;
import core.VibratoParameters;
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.ClampedCoefficientRemodulator;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.GAnalogPhaseDistortionWaveForm;
import cwaves.GSineWaveform;
import cwaves.Inverter;
import cwaves.SineWaveform;


/**
 * 
 * Early attempt to create an agent that emulates the playing of a native-American flute.  This is still a work in progress.
 * 
 * @author tgreen
 *
 */
public class NativeFluteAgentP0 extends IntelligentAgent implements Externalizable {
	
	/**
	 * Stores each interpolated rough-draft waveform under a unique name.
	 */
	public static final HashMap<String,WaveForm> roughDraftWaveformMap = new HashMap<String,WaveForm>();

	/**
	 * The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	protected double minDecayTimeBeats = 4.0;

	/**
	 * For an instrument amplitude decay that is interrupted before minDecayTimeBeats, the number of waves over which the interruption of the amplitude takes place.
	 */
	protected double cutoffTimeWaves = 2.5;

	/**
	 * The duration of the initial attack of a note in waves.
	 */
	protected double attackTimeWaves = 3.0 /* 5.0 */;
	
	/**
	 * The amount of distortion to be used in the modeling of the timbre.
	 */
	protected final double distortionCoeff = 2.0; // 0.51;
	
	/**
	 * Sets the duration of the initial attack of a note in waves.
	 * @param in The duration of the initial attack of a note in waves.
	 */
	public void setAttackTimeWaves( double in )
	{
		attackTimeWaves = in;
	}
	
	
	/**
	 * Gets interpolation points for the pitch bend of a default note parameterized over the barycentric duration of the note.
	 * @return The default interpolation points for the pitch bend.
	 */
	protected static ArrayList<InterpolationPoint> getInterpBend()
	{
		ArrayList<InterpolationPoint> interps = new ArrayList<InterpolationPoint>();
		interps.add( new InterpolationPoint( 0.0 , 1.0004338873671812 ) );
		interps.add( new InterpolationPoint( 0.001996007984031936 , 1.006868888021842 ) );
		interps.add( new InterpolationPoint( 0.003992015968063872 , 1.0072760835312067 ) );
		interps.add( new InterpolationPoint( 0.005988023952095809 , 1.0105979543180326 ) );
		interps.add( new InterpolationPoint( 0.007984031936127744 , 0.9743705932129685 ) );
		interps.add( new InterpolationPoint( 0.00998003992015968 , 0.9772451997259809 ) );
		interps.add( new InterpolationPoint( 0.011976047904191617 , 0.9999139365835243 ) );
		interps.add( new InterpolationPoint( 0.013972055888223553 , 0.9627896233321033 ) );
		interps.add( new InterpolationPoint( 0.015968063872255488 , 1.0199811554290021 ) );
		interps.add( new InterpolationPoint( 0.017964071856287425 , 1.0072760835312067 ) );
		interps.add( new InterpolationPoint( 0.01996007984031936 , 0.9993942560314353 ) );
		interps.add( new InterpolationPoint( 0.021956087824351298 , 1.0002605404048934 ) );
		interps.add( new InterpolationPoint( 0.023952095808383235 , 1.0199811554290021 ) );
		interps.add( new InterpolationPoint( 0.02594810379241517 , 0.9733580463429309 ) );
		interps.add( new InterpolationPoint( 0.027944111776447105 , 0.986717611803109 ) );
		interps.add( new InterpolationPoint( 0.029940119760479042 , 1.006868888021842 ) );
		interps.add( new InterpolationPoint( 0.031936127744510975 , 1.0087316979759018 ) );
		interps.add( new InterpolationPoint( 0.033932135728542916 , 1.0121752935186763 ) );
		interps.add( new InterpolationPoint( 0.03592814371257485 , 1.014692436351711 ) );
		interps.add( new InterpolationPoint( 0.03792415169660679 , 1.0073342677597292 ) );
		interps.add( new InterpolationPoint( 0.03992015968063872 , 0.9744831634200714 ) );
		interps.add( new InterpolationPoint( 0.041916167664670656 , 1.006519994318543 ) );
		interps.add( new InterpolationPoint( 0.043912175648702596 , 0.9950165989020145 ) );
		interps.add( new InterpolationPoint( 0.04590818363273453 , 0.9950165989020145 ) );
		interps.add( new InterpolationPoint( 0.04790419161676647 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.0499001996007984 , 1.0067525766852234 ) );
		interps.add( new InterpolationPoint( 0.05189620758483034 , 1.0213961301123249 ) );
		interps.add( new InterpolationPoint( 0.05389221556886228 , 0.9954190011369398 ) );
		interps.add( new InterpolationPoint( 0.05588822355289421 , 0.9935233857252483 ) );
		interps.add( new InterpolationPoint( 0.05788423153692615 , 0.9990479523893321 ) );
		interps.add( new InterpolationPoint( 0.059880239520958084 , 1.0142822427511435 ) );
		interps.add( new InterpolationPoint( 0.06187624750499002 , 0.9993942560314353 ) );
		interps.add( new InterpolationPoint( 0.06387225548902195 , 1.00878996628644 ) );
		interps.add( new InterpolationPoint( 0.0658682634730539 , 1.0072760835312067 ) );
		interps.add( new InterpolationPoint( 0.06786427145708583 , 0.9956490183332493 ) );
		interps.add( new InterpolationPoint( 0.06986027944111776 , 1.0145166187852337 ) );
		interps.add( new InterpolationPoint( 0.0718562874251497 , 1.006868888021842 ) );
		interps.add( new InterpolationPoint( 0.07385229540918163 , 1.0141064962596429 ) );
		interps.add( new InterpolationPoint( 0.07584830339321358 , 1.005474038419446 ) );
		interps.add( new InterpolationPoint( 0.07784431137724551 , 0.9701585844348067 ) );
		interps.add( new InterpolationPoint( 0.07984031936127745 , 0.9926629361234172 ) );
		interps.add( new InterpolationPoint( 0.08183632734530938 , 1.0140479208631605 ) );
		interps.add( new InterpolationPoint( 0.08383233532934131 , 1.013989348850028 ) );
		interps.add( new InterpolationPoint( 0.08582834331337326 , 0.9994519849745996 ) );
		interps.add( new InterpolationPoint( 0.08782435129740519 , 0.9733580463429309 ) );
		interps.add( new InterpolationPoint( 0.08982035928143713 , 1.009256233956122 ) );
		interps.add( new InterpolationPoint( 0.09181636726546906 , 1.0082074346102914 ) );
		interps.add( new InterpolationPoint( 0.09381237524950099 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.09580838323353294 , 0.9977791987093743 ) );
		interps.add( new InterpolationPoint( 0.09780439121756487 , 1.0035592748062276 ) );
		interps.add( new InterpolationPoint( 0.0998003992015968 , 1.0004916763635405 ) );
		interps.add( new InterpolationPoint( 0.10179640718562874 , 0.9753841933964998 ) );
		interps.add( new InterpolationPoint( 0.10379241516966067 , 1.0141064962596429 ) );
		interps.add( new InterpolationPoint( 0.10578842315369262 , 0.9965696187552937 ) );
		interps.add( new InterpolationPoint( 0.10778443113772455 , 1.0136965395277735 ) );
		interps.add( new InterpolationPoint( 0.10978043912175649 , 1.0032115279287217 ) );
		interps.add( new InterpolationPoint( 0.11177644710578842 , 0.9727397857604151 ) );
		interps.add( new InterpolationPoint( 0.11377245508982035 , 0.989800149158907 ) );
		interps.add( new InterpolationPoint( 0.1157684630738523 , 1.0090813583301208 ) );
		interps.add( new InterpolationPoint( 0.11776447105788423 , 1.0082656726373154 ) );
		interps.add( new InterpolationPoint( 0.11976047904191617 , 0.9979521156506544 ) );
		interps.add( new InterpolationPoint( 0.1217564870259481 , 1.021455129970276 ) );
		interps.add( new InterpolationPoint( 0.12375249500998003 , 1.0005494686980194 ) );
		interps.add( new InterpolationPoint( 0.12574850299401197 , 0.9733580463429309 ) );
		interps.add( new InterpolationPoint( 0.1277445109780439 , 1.0070433802227339 ) );
		interps.add( new InterpolationPoint( 0.12974051896207583 , 0.9782052852670329 ) );
		interps.add( new InterpolationPoint( 0.1317365269461078 , 0.9960516763297291 ) );
		interps.add( new InterpolationPoint( 0.13373253493013973 , 1.0141064962596429 ) );
		interps.add( new InterpolationPoint( 0.13572854291417166 , 1.0176860011273292 ) );
		interps.add( new InterpolationPoint( 0.1377245508982036 , 1.0024584896836088 ) );
		interps.add( new InterpolationPoint( 0.13972055888223553 , 1.0085569132371845 ) );
		interps.add( new InterpolationPoint( 0.14171656686626746 , 1.013520894522114 ) );
		interps.add( new InterpolationPoint( 0.1437125748502994 , 0.9977791987093743 ) );
		interps.add( new InterpolationPoint( 0.14570858283433133 , 1.01645228871069 ) );
		interps.add( new InterpolationPoint( 0.14770459081836326 , 1.0007228657321037 ) );
		interps.add( new InterpolationPoint( 0.1497005988023952 , 1.0013010729028928 ) );
		interps.add( new InterpolationPoint( 0.15169660678642716 , 0.9625671973911168 ) );
		interps.add( new InterpolationPoint( 0.1536926147704591 , 1.0084986583879294 ) );
		interps.add( new InterpolationPoint( 0.15568862275449102 , 0.9660763824001379 ) );
		interps.add( new InterpolationPoint( 0.15768463073852296 , 0.9972030252654711 ) );
		interps.add( new InterpolationPoint( 0.1596806387225549 , 0.9699344560989588 ) );
		interps.add( new InterpolationPoint( 0.16167664670658682 , 1.00083848044896 ) );
		interps.add( new InterpolationPoint( 0.16367265469061876 , 0.9743143129859619 ) );
		interps.add( new InterpolationPoint( 0.1656686626746507 , 0.993293859597622 ) );
		interps.add( new InterpolationPoint( 0.16766467065868262 , 1.0084986583879294 ) );
		interps.add( new InterpolationPoint( 0.16966067864271456 , 1.0069270487291502 ) );
		interps.add( new InterpolationPoint( 0.17165668662674652 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.17365269461077845 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.17564870259481039 , 0.9625671973911168 ) );
		interps.add( new InterpolationPoint( 0.17764471057884232 , 0.9933512361578933 ) );
		interps.add( new InterpolationPoint( 0.17964071856287425 , 1.0148096649898708 ) );
		interps.add( new InterpolationPoint( 0.18163672654690619 , 0.9951315543669979 ) );
		interps.add( new InterpolationPoint( 0.18363273453093812 , 0.9992788081482887 ) );
		interps.add( new InterpolationPoint( 0.18562874251497005 , 1.0163348703201573 ) );
		interps.add( new InterpolationPoint( 0.18762475049900199 , 1.021927251540725 ) );
		interps.add( new InterpolationPoint( 0.18962075848303392 , 1.0082656726373154 ) );
		interps.add( new InterpolationPoint( 0.19161676646706588 , 0.994442020737555 ) );
		interps.add( new InterpolationPoint( 0.1936127744510978 , 1.0144580196998296 ) );
		interps.add( new InterpolationPoint( 0.19560878243512975 , 1.00083848044896 ) );
		interps.add( new InterpolationPoint( 0.19760479041916168 , 0.971504442471964 ) );
		interps.add( new InterpolationPoint( 0.1996007984031936 , 0.9978944733408377 ) );
		interps.add( new InterpolationPoint( 0.20159680638722555 , 1.0047773384103071 ) );
		interps.add( new InterpolationPoint( 0.20359281437125748 , 1.0082656726373154 ) );
		interps.add( new InterpolationPoint( 0.2055888223552894 , 1.0107730927728642 ) );
		interps.add( new InterpolationPoint( 0.20758483033932135 , 1.0139307802200497 ) );
		interps.add( new InterpolationPoint( 0.20958083832335328 , 1.0062874656835923 ) );
		interps.add( new InterpolationPoint( 0.21157684630738524 , 1.0069852127960495 ) );
		interps.add( new InterpolationPoint( 0.21357285429141717 , 1.0142822427511438 ) );
		interps.add( new InterpolationPoint( 0.2155688622754491 , 0.9931791164192011 ) );
		interps.add( new InterpolationPoint( 0.21756487025948104 , 0.9935807755438517 ) );
		interps.add( new InterpolationPoint( 0.21956087824351297 , 0.9984133740126224 ) );
		interps.add( new InterpolationPoint( 0.2215568862275449 , 1.018097571267724 ) );
		interps.add( new InterpolationPoint( 0.22355289421157684 , 1.0162761662114592 ) );
		interps.add( new InterpolationPoint( 0.22554890219560877 , 0.9742017622840997 ) );
		interps.add( new InterpolationPoint( 0.2275449101796407 , 1.013989348850028 ) );
		interps.add( new InterpolationPoint( 0.22954091816367264 , 0.9973758423548504 ) );
		interps.add( new InterpolationPoint( 0.2315369261477046 , 1.0196277181474989 ) );
		interps.add( new InterpolationPoint( 0.23353293413173654 , 0.9969150633235541 ) );
		interps.add( new InterpolationPoint( 0.23552894211576847 , 1.0016481575523926 ) );
		interps.add( new InterpolationPoint( 0.2375249500998004 , 1.0161587681662203 ) );
		interps.add( new InterpolationPoint( 0.23952095808383234 , 0.9927202762390671 ) );
		interps.add( new InterpolationPoint( 0.24151696606786427 , 1.013520894522114 ) );
		interps.add( new InterpolationPoint( 0.2435129740518962 , 0.9982980394388652 ) );
		interps.add( new InterpolationPoint( 0.24550898203592814 , 1.0067525766852234 ) );
		interps.add( new InterpolationPoint( 0.24750499001996007 , 1.0071597251562352 ) );
		interps.add( new InterpolationPoint( 0.249500998003992 , 0.9733580463429308 ) );
		interps.add( new InterpolationPoint( 0.25149700598802394 , 0.9896858095664302 ) );
		interps.add( new InterpolationPoint( 0.25349301397205587 , 1.0087316979759016 ) );
		interps.add( new InterpolationPoint( 0.2554890219560878 , 1.0151614321720037 ) );
		interps.add( new InterpolationPoint( 0.25748502994011974 , 0.9970878305086077 ) );
		interps.add( new InterpolationPoint( 0.25948103792415167 , 1.0042551302253349 ) );
		interps.add( new InterpolationPoint( 0.26147704590818366 , 1.000202764759085 ) );
		interps.add( new InterpolationPoint( 0.2634730538922156 , 1.0144580196998296 ) );
		interps.add( new InterpolationPoint( 0.2654690618762475 , 0.9973758423548506 ) );
		interps.add( new InterpolationPoint( 0.26746506986027946 , 0.977527479968095 ) );
		interps.add( new InterpolationPoint( 0.2694610778443114 , 0.9980674102594201 ) );
		interps.add( new InterpolationPoint( 0.2714570858283433 , 1.0182740094197484 ) );
		interps.add( new InterpolationPoint( 0.27345309381237526 , 1.0224586491498973 ) );
		interps.add( new InterpolationPoint( 0.2754491017964072 , 1.0018217449912437 ) );
		interps.add( new InterpolationPoint( 0.2774451097804391 , 1.0141064962596429 ) );
		interps.add( new InterpolationPoint( 0.27944111776447106 , 1.0010697499555388 ) );
		interps.add( new InterpolationPoint( 0.281437125748503 , 0.9976639373941892 ) );
		interps.add( new InterpolationPoint( 0.2834331337325349 , 0.9696543684914868 ) );
		interps.add( new InterpolationPoint( 0.28542914171656686 , 0.9884289454590707 ) );
		interps.add( new InterpolationPoint( 0.2874251497005988 , 1.0067525766852234 ) );
		interps.add( new InterpolationPoint( 0.2894211576846307 , 1.0073342677597292 ) );
		interps.add( new InterpolationPoint( 0.29141716566866266 , 1.0124091826878838 ) );
		interps.add( new InterpolationPoint( 0.2934131736526946 , 1.01434083168298 ) );
		interps.add( new InterpolationPoint( 0.2954091816367265 , 1.0141064962596429 ) );
		interps.add( new InterpolationPoint( 0.29740518962075846 , 0.9715044424719642 ) );
		interps.add( new InterpolationPoint( 0.2994011976047904 , 1.0078580770824748 ) );
		interps.add( new InterpolationPoint( 0.3013972055888224 , 0.9772451997259809 ) );
		interps.add( new InterpolationPoint( 0.3033932135728543 , 0.9960516763297289 ) );
		interps.add( new InterpolationPoint( 0.30538922155688625 , 1.0104228462097533 ) );
		interps.add( new InterpolationPoint( 0.3073852295409182 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.3093812375249501 , 0.9994519849745996 ) );
		interps.add( new InterpolationPoint( 0.31137724550898205 , 0.9733580463429309 ) );
		interps.add( new InterpolationPoint( 0.313373253493014 , 1.014633827110979 ) );
		interps.add( new InterpolationPoint( 0.3153692614770459 , 0.9951890370797476 ) );
		interps.add( new InterpolationPoint( 0.31736526946107785 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.3193612774451098 , 0.9960516763297291 ) );
		interps.add( new InterpolationPoint( 0.3213572854291417 , 1.002574304919189 ) );
		interps.add( new InterpolationPoint( 0.32335329341317365 , 1.0146924363517107 ) );
		interps.add( new InterpolationPoint( 0.3253493013972056 , 0.9753841933964998 ) );
		interps.add( new InterpolationPoint( 0.3273453093812375 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.32934131736526945 , 1.0047773384103071 ) );
		interps.add( new InterpolationPoint( 0.3313373253493014 , 1.0072760835312067 ) );
		interps.add( new InterpolationPoint( 0.3333333333333333 , 0.9995097172524146 ) );
		interps.add( new InterpolationPoint( 0.33532934131736525 , 0.9722903883746433 ) );
		interps.add( new InterpolationPoint( 0.3373253493013972 , 0.9911160043101236 ) );
		interps.add( new InterpolationPoint( 0.3393213572854291 , 1.0097227171370078 ) );
		interps.add( new InterpolationPoint( 0.3413173652694611 , 1.0215731399105679 ) );
		interps.add( new InterpolationPoint( 0.34331337325349304 , 1.012058369197464 ) );
		interps.add( new InterpolationPoint( 0.34530938123752497 , 1.0072179026634402 ) );
		interps.add( new InterpolationPoint( 0.3473053892215569 , 1.0144580196998296 ) );
		interps.add( new InterpolationPoint( 0.34930139720558884 , 0.9734142713322147 ) );
		interps.add( new InterpolationPoint( 0.35129740518962077 , 1.0141064962596429 ) );
		interps.add( new InterpolationPoint( 0.3532934131736527 , 0.9943845811729154 ) );
		interps.add( new InterpolationPoint( 0.35528942115768464 , 1.00083848044896 ) );
		interps.add( new InterpolationPoint( 0.35728542914171657 , 1.0126431259032076 ) );
		interps.add( new InterpolationPoint( 0.3592814371257485 , 1.0072179026634402 ) );
		interps.add( new InterpolationPoint( 0.36127744510978044 , 1.0007228657321037 ) );
		interps.add( new InterpolationPoint( 0.36327345309381237 , 0.993293859597622 ) );
		interps.add( new InterpolationPoint( 0.3652694610778443 , 1.0070433802227339 ) );
		interps.add( new InterpolationPoint( 0.36726546906187624 , 0.9980097612901179 ) );
		interps.add( new InterpolationPoint( 0.36926147704590817 , 1.0081491999471306 ) );
		interps.add( new InterpolationPoint( 0.3712574850299401 , 1.0006650633821081 ) );
		interps.add( new InterpolationPoint( 0.37325349301397204 , 1.0069270487291502 ) );
		interps.add( new InterpolationPoint( 0.37524950099800397 , 1.0008962928162073 ) );
		interps.add( new InterpolationPoint( 0.3772455089820359 , 1.0085569132371843 ) );
		interps.add( new InterpolationPoint( 0.37924151696606784 , 1.007101551009398 ) );
		interps.add( new InterpolationPoint( 0.3812375249500998 , 0.9982403771482838 ) );
		interps.add( new InterpolationPoint( 0.38323353293413176 , 0.9699904833282043 ) );
		interps.add( new InterpolationPoint( 0.3852295409181637 , 1.0108898686045444 ) );
		interps.add( new InterpolationPoint( 0.3872255489021956 , 0.9920897170953186 ) );
		interps.add( new InterpolationPoint( 0.38922155688622756 , 0.993236486351455 ) );
		interps.add( new InterpolationPoint( 0.3912175648702595 , 1.0125261475390006 ) );
		interps.add( new InterpolationPoint( 0.3932135728542914 , 1.0146924363517107 ) );
		interps.add( new InterpolationPoint( 0.39520958083832336 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.3972055888223553 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.3992015968063872 , 0.9626227990585214 ) );
		interps.add( new InterpolationPoint( 0.40119760479041916 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.4031936127744511 , 1.0146924363517107 ) );
		interps.add( new InterpolationPoint( 0.405189620758483 , 0.9837014935873478 ) );
		interps.add( new InterpolationPoint( 0.40718562874251496 , 0.9996251918127675 ) );
		interps.add( new InterpolationPoint( 0.4091816367265469 , 1.0147510489779428 ) );
		interps.add( new InterpolationPoint( 0.4111776447105788 , 1.0086151714514686 ) );
		interps.add( new InterpolationPoint( 0.41317365269461076 , 0.9627340120288291 ) );
		interps.add( new InterpolationPoint( 0.4151696606786427 , 0.9952465231129244 ) );
		interps.add( new InterpolationPoint( 0.4171656686626746 , 1.0144580196998296 ) );
		interps.add( new InterpolationPoint( 0.41916167664670656 , 1.0140479208631605 ) );
		interps.add( new InterpolationPoint( 0.42115768463073855 , 0.971672805950427 ) );
		interps.add( new InterpolationPoint( 0.4231536926147705 , 0.9915168290759059 ) );
		interps.add( new InterpolationPoint( 0.4251497005988024 , 1.0084404069035096 ) );
		interps.add( new InterpolationPoint( 0.42714570858283435 , 1.001127575681822 ) );
		interps.add( new InterpolationPoint( 0.4291417165668663 , 0.9962242938882538 ) );
		interps.add( new InterpolationPoint( 0.4311377245508982 , 1.0140479208631605 ) );
		interps.add( new InterpolationPoint( 0.43313373253493015 , 1.0009541085229243 ) );
		interps.add( new InterpolationPoint( 0.4351297405189621 , 0.9722342283015097 ) );
		interps.add( new InterpolationPoint( 0.437125748502994 , 1.0143994239991436 ) );
		interps.add( new InterpolationPoint( 0.43912175648702595 , 0.993236486351455 ) );
		interps.add( new InterpolationPoint( 0.4411177644710579 , 0.9995674528650731 ) );
		interps.add( new InterpolationPoint( 0.4431137724550898 , 0.9983557050602472 ) );
		interps.add( new InterpolationPoint( 0.44510978043912175 , 1.0213961301123247 ) );
		interps.add( new InterpolationPoint( 0.4471057884231537 , 1.0162761662114592 ) );
		interps.add( new InterpolationPoint( 0.4491017964071856 , 1.00878996628644 ) );
		interps.add( new InterpolationPoint( 0.45109780439121755 , 0.964626599566812 ) );
		interps.add( new InterpolationPoint( 0.4530938123752495 , 0.9981827181883107 ) );
		interps.add( new InterpolationPoint( 0.4550898203592814 , 1.0199811554290021 ) );
		interps.add( new InterpolationPoint( 0.45708582834331335 , 0.9972030252654711 ) );
		interps.add( new InterpolationPoint( 0.4590818363273453 , 1.0058806698270681 ) );
		interps.add( new InterpolationPoint( 0.46107784431137727 , 1.0018796141557185 ) );
		interps.add( new InterpolationPoint( 0.4630738522954092 , 1.0148096649898706 ) );
		interps.add( new InterpolationPoint( 0.46506986027944114 , 1.0105395815770266 ) );
		interps.add( new InterpolationPoint( 0.46706586826347307 , 0.9984133740126224 ) );
		interps.add( new InterpolationPoint( 0.469061876247505 , 1.0066944260555244 ) );
		interps.add( new InterpolationPoint( 0.47105788423153694 , 1.0018796141557185 ) );
		interps.add( new InterpolationPoint( 0.47305389221556887 , 0.993236486351455 ) );
		interps.add( new InterpolationPoint( 0.4750499001996008 , 0.9944420207375552 ) );
		interps.add( new InterpolationPoint( 0.47704590818363274 , 1.0069852127960495 ) );
		interps.add( new InterpolationPoint( 0.47904191616766467 , 1.0152787149938152 ) );
		interps.add( new InterpolationPoint( 0.4810379241516966 , 1.0123507053292464 ) );
		interps.add( new InterpolationPoint( 0.48303393213572854 , 0.9969150633235542 ) );
		interps.add( new InterpolationPoint( 0.48502994011976047 , 1.001185404748347 ) );
		interps.add( new InterpolationPoint( 0.4870259481037924 , 0.9745394534005429 ) );
		interps.add( new InterpolationPoint( 0.48902195608782434 , 1.0041391208244164 ) );
		interps.add( new InterpolationPoint( 0.49101796407185627 , 0.9949591261493971 ) );
		interps.add( new InterpolationPoint( 0.4930139720558882 , 1.0013010729028928 ) );
		interps.add( new InterpolationPoint( 0.49500998003992014 , 1.0160413836825517 ) );
		interps.add( new InterpolationPoint( 0.49700598802395207 , 1.0146338271109787 ) );
		interps.add( new InterpolationPoint( 0.499001996007984 , 1.0215731399105679 ) );
		interps.add( new InterpolationPoint( 0.500998003992016 , 0.9950740749744843 ) );
		interps.add( new InterpolationPoint( 0.5029940119760479 , 1.001185404748347 ) );
		interps.add( new InterpolationPoint( 0.5049900199600799 , 1.013520894522114 ) );
		interps.add( new InterpolationPoint( 0.5069860279441117 , 0.9712239015001186 ) );
		interps.add( new InterpolationPoint( 0.5089820359281437 , 0.9994519849745996 ) );
		interps.add( new InterpolationPoint( 0.5109780439121756 , 0.9931791164192011 ) );
		interps.add( new InterpolationPoint( 0.5129740518962076 , 1.0149269071715965 ) );
		interps.add( new InterpolationPoint( 0.5149700598802395 , 0.9949591261493971 ) );
		interps.add( new InterpolationPoint( 0.5169660678642715 , 1.0229312345518327 ) );
		interps.add( new InterpolationPoint( 0.5189620758483033 , 1.0073342677597292 ) );
		interps.add( new InterpolationPoint( 0.5209580838323353 , 0.972908363326106 ) );
		interps.add( new InterpolationPoint( 0.5229540918163673 , 1.0124091826878838 ) );
		interps.add( new InterpolationPoint( 0.5249500998003992 , 0.9940974331120372 ) );
		interps.add( new InterpolationPoint( 0.5269461077844312 , 0.9949591261493971 ) );
		interps.add( new InterpolationPoint( 0.5289421157684631 , 1.0123507053292464 ) );
		interps.add( new InterpolationPoint( 0.530938123752495 , 1.0142822427511438 ) );
		interps.add( new InterpolationPoint( 0.5329341317365269 , 0.9995674528650731 ) );
		interps.add( new InterpolationPoint( 0.5349301397205589 , 0.9750462095662098 ) );
		interps.add( new InterpolationPoint( 0.5369261477045908 , 0.9998561809577596 ) );
		interps.add( new InterpolationPoint( 0.5389221556886228 , 1.0001449924504324 ) );
		interps.add( new InterpolationPoint( 0.5409181636726547 , 1.0142822427511435 ) );
		interps.add( new InterpolationPoint( 0.5429141716566867 , 0.9850661391025749 ) );
		interps.add( new InterpolationPoint( 0.5449101796407185 , 1.0062293419189827 ) );
		interps.add( new InterpolationPoint( 0.5469061876247505 , 1.0004338873671812 ) );
		interps.add( new InterpolationPoint( 0.5489021956087824 , 1.0080327407116214 ) );
		interps.add( new InterpolationPoint( 0.5508982035928144 , 1.015982696525817 ) );
		interps.add( new InterpolationPoint( 0.5528942115768463 , 0.9978944733408377 ) );
		interps.add( new InterpolationPoint( 0.5548902195608783 , 1.0073924553492013 ) );
		interps.add( new InterpolationPoint( 0.5568862275449101 , 1.0093145325659216 ) );
		interps.add( new InterpolationPoint( 0.5588822355289421 , 0.9910587568581779 ) );
		interps.add( new InterpolationPoint( 0.5608782435129741 , 0.994442020737555 ) );
		interps.add( new InterpolationPoint( 0.562874251497006 , 0.9950165989020147 ) );
		interps.add( new InterpolationPoint( 0.564870259481038 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.5668662674650699 , 0.9981250625587538 ) );
		interps.add( new InterpolationPoint( 0.5688622754491018 , 0.9719534765896855 ) );
		interps.add( new InterpolationPoint( 0.5708582834331337 , 1.0021111242430534 ) );
		interps.add( new InterpolationPoint( 0.5728542914171657 , 0.9745394534005432 ) );
		interps.add( new InterpolationPoint( 0.5748502994011976 , 1.0140479208631605 ) );
		interps.add( new InterpolationPoint( 0.5768463073852296 , 0.9846110470479421 ) );
		interps.add( new InterpolationPoint( 0.5788423153692615 , 0.9987017687461295 ) );
		interps.add( new InterpolationPoint( 0.5808383233532934 , 1.0145166187852337 ) );
		interps.add( new InterpolationPoint( 0.5828343313373253 , 1.0257120883910984 ) );
		interps.add( new InterpolationPoint( 0.5848303393213573 , 0.9641809504030407 ) );
		interps.add( new InterpolationPoint( 0.5868263473053892 , 0.9949591261493971 ) );
		interps.add( new InterpolationPoint( 0.5888223552894212 , 1.0071597251562352 ) );
		interps.add( new InterpolationPoint( 0.590818363273453 , 1.0163935778198356 ) );
		interps.add( new InterpolationPoint( 0.592814371257485 , 0.9707191318845872 ) );
		interps.add( new InterpolationPoint( 0.5948103792415169 , 0.9874588256353637 ) );
		interps.add( new InterpolationPoint( 0.5968063872255489 , 1.0072760835312067 ) );
		interps.add( new InterpolationPoint( 0.5988023952095808 , 1.0124676634244036 ) );
		interps.add( new InterpolationPoint( 0.6007984031936128 , 0.997836834360475 ) );
		interps.add( new InterpolationPoint( 0.6027944111776448 , 1.0213961301123247 ) );
		interps.add( new InterpolationPoint( 0.6047904191616766 , 1.0004338873671812 ) );
		interps.add( new InterpolationPoint( 0.6067864271457086 , 0.9726835997298204 ) );
		interps.add( new InterpolationPoint( 0.6087824351297405 , 1.0136965395277735 ) );
		interps.add( new InterpolationPoint( 0.6107784431137725 , 0.99283496640713 ) );
		interps.add( new InterpolationPoint( 0.6127744510978044 , 0.9960516763297291 ) );
		interps.add( new InterpolationPoint( 0.6147704590818364 , 1.0124091826878838 ) );
		interps.add( new InterpolationPoint( 0.6167664670658682 , 1.0150441628984523 ) );
		interps.add( new InterpolationPoint( 0.6187624750499002 , 1.0006650633821081 ) );
		interps.add( new InterpolationPoint( 0.6207584830339321 , 1.014633827110979 ) );
		interps.add( new InterpolationPoint( 0.6227544910179641 , 1.011766117483477 ) );
		interps.add( new InterpolationPoint( 0.624750499001996 , 0.9979521156506544 ) );
		interps.add( new InterpolationPoint( 0.626746506986028 , 0.969038460422555 ) );
		interps.add( new InterpolationPoint( 0.6287425149700598 , 0.9983557050602472 ) );
		interps.add( new InterpolationPoint( 0.6307385229540918 , 1.0070433802227339 ) );
		interps.add( new InterpolationPoint( 0.6327345309381237 , 1.0123507053292464 ) );
		interps.add( new InterpolationPoint( 0.6347305389221557 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.6367265469061876 , 1.0165110029929165 ) );
		interps.add( new InterpolationPoint( 0.6387225548902196 , 0.9967423260741785 ) );
		interps.add( new InterpolationPoint( 0.6407185628742516 , 0.9995674528650731 ) );
		interps.add( new InterpolationPoint( 0.6427145708582834 , 1.00078067142099 ) );
		interps.add( new InterpolationPoint( 0.6447105788423154 , 0.9743143129859619 ) );
		interps.add( new InterpolationPoint( 0.6467065868263473 , 0.9638468486481465 ) );
		interps.add( new InterpolationPoint( 0.6487025948103793 , 1.0115908069654844 ) );
		interps.add( new InterpolationPoint( 0.6506986027944112 , 1.0146924363517107 ) );
		interps.add( new InterpolationPoint( 0.6526946107784432 , 1.0134038147599573 ) );
		interps.add( new InterpolationPoint( 0.654690618762475 , 1.0144580196998296 ) );
		interps.add( new InterpolationPoint( 0.656686626746507 , 1.0065781348723783 ) );
		interps.add( new InterpolationPoint( 0.6586826347305389 , 0.993293859597622 ) );
		interps.add( new InterpolationPoint( 0.6606786427145709 , 1.0148096649898706 ) );
		interps.add( new InterpolationPoint( 0.6626746506986028 , 0.9951315543669979 ) );
		interps.add( new InterpolationPoint( 0.6646706586826348 , 0.9992210892079216 ) );
		interps.add( new InterpolationPoint( 0.6666666666666666 , 1.016569720666711 ) );
		interps.add( new InterpolationPoint( 0.6686626746506986 , 1.0140479208631605 ) );
		interps.add( new InterpolationPoint( 0.6706586826347305 , 0.9627340120288291 ) );
		interps.add( new InterpolationPoint( 0.6726546906187625 , 1.0072760835312067 ) );
		interps.add( new InterpolationPoint( 0.6746506986027944 , 1.0143994239991436 ) );
		interps.add( new InterpolationPoint( 0.6766467065868264 , 1.0062293419189827 ) );
		interps.add( new InterpolationPoint( 0.6786427145708582 , 0.971448327795535 ) );
		interps.add( new InterpolationPoint( 0.6806387225548902 , 1.0024584896836088 ) );
		interps.add( new InterpolationPoint( 0.6826347305389222 , 1.0084404069035096 ) );
		interps.add( new InterpolationPoint( 0.6846307385229541 , 0.9650724547118121 ) );
		interps.add( new InterpolationPoint( 0.6866267465069861 , 0.9961667513784531 ) );
		interps.add( new InterpolationPoint( 0.688622754491018 , 1.0140479208631605 ) );
		interps.add( new InterpolationPoint( 0.6906187624750499 , 1.00083848044896 ) );
		interps.add( new InterpolationPoint( 0.6926147704590818 , 0.9723465516918035 ) );
		interps.add( new InterpolationPoint( 0.6946107784431138 , 1.0142822427511435 ) );
		interps.add( new InterpolationPoint( 0.6966067864271457 , 0.9769065710317985 ) );
		interps.add( new InterpolationPoint( 0.6986027944111777 , 0.9972606276345275 ) );
		interps.add( new InterpolationPoint( 0.7005988023952096 , 0.9985287219111216 ) );
		interps.add( new InterpolationPoint( 0.7025948103792415 , 1.018097571267724 ) );
		interps.add( new InterpolationPoint( 0.7045908183632734 , 1.0010697499555388 ) );
		interps.add( new InterpolationPoint( 0.7065868263473054 , 0.9751588578280826 ) );
		interps.add( new InterpolationPoint( 0.7085828343313373 , 0.9644594569970846 ) );
		interps.add( new InterpolationPoint( 0.7105788423153693 , 0.9973758423548504 ) );
		interps.add( new InterpolationPoint( 0.7125748502994012 , 1.0258898461527237 ) );
		interps.add( new InterpolationPoint( 0.7145708582834331 , 0.997836834360475 ) );
		interps.add( new InterpolationPoint( 0.716566866267465 , 1.0076834437176063 ) );
		interps.add( new InterpolationPoint( 0.718562874251497 , 1.0012432371553064 ) );
		interps.add( new InterpolationPoint( 0.720558882235529 , 0.99283496640713 ) );
		interps.add( new InterpolationPoint( 0.7225548902195609 , 0.9659647833263663 ) );
		interps.add( new InterpolationPoint( 0.7245508982035929 , 0.9985287219111216 ) );
		interps.add( new InterpolationPoint( 0.7265469061876247 , 0.9701585844348067 ) );
		interps.add( new InterpolationPoint( 0.7285429141716567 , 1.001185404748347 ) );
		interps.add( new InterpolationPoint( 0.7305389221556886 , 0.993236486351455 ) );
		interps.add( new InterpolationPoint( 0.7325349301397206 , 0.9955915090517079 ) );
		interps.add( new InterpolationPoint( 0.7345309381237525 , 0.9955915090517079 ) );
		interps.add( new InterpolationPoint( 0.7365269461077845 , 1.0211601647572206 ) );
		interps.add( new InterpolationPoint( 0.7385229540918163 , 1.0140479208631605 ) );
		interps.add( new InterpolationPoint( 0.7405189620758483 , 0.9722903883746431 ) );
		interps.add( new InterpolationPoint( 0.7425149700598802 , 1.0013010729028928 ) );
		interps.add( new InterpolationPoint( 0.7445109780439122 , 0.9968574809144987 ) );
		interps.add( new InterpolationPoint( 0.7465069860279441 , 0.9662438051859644 ) );
		interps.add( new InterpolationPoint( 0.7485029940119761 , 0.9957640468624105 ) );
		interps.add( new InterpolationPoint( 0.7504990019960079 , 1.0004916763635405 ) );
		interps.add( new InterpolationPoint( 0.7524950099800399 , 1.0160413836825517 ) );
		interps.add( new InterpolationPoint( 0.7544910179640718 , 1.0083821587837298 ) );
		interps.add( new InterpolationPoint( 0.7564870259481038 , 1.00228479191487 ) );
		interps.add( new InterpolationPoint( 0.7584830339321357 , 0.9960516763297291 ) );
		interps.add( new InterpolationPoint( 0.7604790419161677 , 0.9678078176793901 ) );
		interps.add( new InterpolationPoint( 0.7624750499001997 , 1.0022268993470864 ) );
		interps.add( new InterpolationPoint( 0.7644710578842315 , 0.9711678030279036 ) );
		interps.add( new InterpolationPoint( 0.7664670658682635 , 1.0021111242430534 ) );
		interps.add( new InterpolationPoint( 0.7684630738522954 , 1.0083821587837298 ) );
		interps.add( new InterpolationPoint( 0.7704590818363274 , 1.0165697206667108 ) );
		interps.add( new InterpolationPoint( 0.7724550898203593 , 0.9964544971681936 ) );
		interps.add( new InterpolationPoint( 0.7744510978043913 , 1.0258898461527237 ) );
		interps.add( new InterpolationPoint( 0.7764471057884231 , 1.0019374866629438 ) );
		interps.add( new InterpolationPoint( 0.7784431137724551 , 0.9730207646000302 ) );
		interps.add( new InterpolationPoint( 0.780439121756487 , 0.9642366452871667 ) );
		interps.add( new InterpolationPoint( 0.782435129740519 , 0.9934659992215151 ) );
		interps.add( new InterpolationPoint( 0.7844311377245509 , 0.9675283441804934 ) );
		interps.add( new InterpolationPoint( 0.7864271457085829 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.7884231536926147 , 1.0142236572034389 ) );
		interps.add( new InterpolationPoint( 0.7904191616766467 , 1.0258898461527237 ) );
		interps.add( new InterpolationPoint( 0.7924151696606786 , 0.9949016567164399 ) );
		interps.add( new InterpolationPoint( 0.7944111776447106 , 0.9997984286679943 ) );
		interps.add( new InterpolationPoint( 0.7964071856287425 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.7984031936127745 , 1.014223657203439 ) );
		interps.add( new InterpolationPoint( 0.8003992015968064 , 0.9980674102594204 ) );
		interps.add( new InterpolationPoint( 0.8023952095808383 , 1.0017638791693269 ) );
		interps.add( new InterpolationPoint( 0.8043912175648703 , 1.0018217449912439 ) );
		interps.add( new InterpolationPoint( 0.8063872255489022 , 0.9931791164192011 ) );
		interps.add( new InterpolationPoint( 0.8083832335329342 , 1.0144580196998296 ) );
		interps.add( new InterpolationPoint( 0.810379241516966 , 0.9978944733408377 ) );
		interps.add( new InterpolationPoint( 0.812375249500998 , 0.971672805950427 ) );
		interps.add( new InterpolationPoint( 0.8143712574850299 , 1.0076834437176063 ) );
		interps.add( new InterpolationPoint( 0.8163672654690619 , 0.993465999221515 ) );
		interps.add( new InterpolationPoint( 0.8183632734530938 , 1.0030956390825048 ) );
		interps.add( new InterpolationPoint( 0.8203592814371258 , 1.0115908069654844 ) );
		interps.add( new InterpolationPoint( 0.8223552894211577 , 0.9699904833282043 ) );
		interps.add( new InterpolationPoint( 0.8243512974051896 , 1.0013010729028928 ) );
		interps.add( new InterpolationPoint( 0.8263473053892215 , 0.9733580463429308 ) );
		interps.add( new InterpolationPoint( 0.8283433133732535 , 1.0143994239991436 ) );
		interps.add( new InterpolationPoint( 0.8303393213572854 , 0.999682934095691 ) );
		interps.add( new InterpolationPoint( 0.8323353293413174 , 1.0139307802200497 ) );
		interps.add( new InterpolationPoint( 0.8343313373253493 , 0.9846110470479421 ) );
		interps.add( new InterpolationPoint( 0.8363273453093812 , 0.9991056613286357 ) );
		interps.add( new InterpolationPoint( 0.8383233532934131 , 1.0162174654935456 ) );
		interps.add( new InterpolationPoint( 0.8403193612774451 , 0.9760041347296429 ) );
		interps.add( new InterpolationPoint( 0.8423153692614771 , 1.002863901550218 ) );
		interps.add( new InterpolationPoint( 0.844311377245509 , 0.9965696187552939 ) );
		interps.add( new InterpolationPoint( 0.846307385229541 , 0.9975486893937443 ) );
		interps.add( new InterpolationPoint( 0.8483033932135728 , 1.0076252393204723 ) );
		interps.add( new InterpolationPoint( 0.8502994011976048 , 0.9727397857604151 ) );
		interps.add( new InterpolationPoint( 0.8522954091816367 , 0.9901432472011865 ) );
		interps.add( new InterpolationPoint( 0.8542914171656687 , 1.0072179026634402 ) );
		interps.add( new InterpolationPoint( 0.8562874251497006 , 1.0122922313482963 ) );
		interps.add( new InterpolationPoint( 0.8582834331337326 , 1.0145752212555517 ) );
		interps.add( new InterpolationPoint( 0.8602794411177644 , 1.0165110029929165 ) );
		interps.add( new InterpolationPoint( 0.8622754491017964 , 1.0019953625131124 ) );
		interps.add( new InterpolationPoint( 0.8642714570858283 , 1.0082656726373151 ) );
		interps.add( new InterpolationPoint( 0.8662674650698603 , 1.0172745973658603 ) );
		interps.add( new InterpolationPoint( 0.8682634730538922 , 0.9807512435738402 ) );
		interps.add( new InterpolationPoint( 0.8702594810379242 , 0.995304012466721 ) );
		interps.add( new InterpolationPoint( 0.872255489021956 , 1.0141650750396707 ) );
		interps.add( new InterpolationPoint( 0.874251497005988 , 1.0200989950792414 ) );
		interps.add( new InterpolationPoint( 0.8762475049900199 , 1.0075088406117718 ) );
		interps.add( new InterpolationPoint( 0.8782435129740519 , 0.9955340030919371 ) );
		interps.add( new InterpolationPoint( 0.8802395209580839 , 1.0023426878267565 ) );
		interps.add( new InterpolationPoint( 0.8822355289421158 , 1.0162761662114592 ) );
		interps.add( new InterpolationPoint( 0.8842315369261478 , 1.0152787149938152 ) );
		interps.add( new InterpolationPoint( 0.8862275449101796 , 0.9888858021650728 ) );
		interps.add( new InterpolationPoint( 0.8882235528942116 , 1.0072760835312067 ) );
		interps.add( new InterpolationPoint( 0.8902195608782435 , 1.0162174654935452 ) );
		interps.add( new InterpolationPoint( 0.8922155688622755 , 1.0143994239991434 ) );
		interps.add( new InterpolationPoint( 0.8942115768463074 , 1.0065781348723783 ) );
		interps.add( new InterpolationPoint( 0.8962075848303394 , 0.9995097172524146 ) );
		interps.add( new InterpolationPoint( 0.8982035928143712 , 0.9720096204454955 ) );
		interps.add( new InterpolationPoint( 0.9001996007984032 , 1.0036172443372589 ) );
		interps.add( new InterpolationPoint( 0.9021956087824351 , 0.9927776196669075 ) );
		interps.add( new InterpolationPoint( 0.9041916167664671 , 0.9979521156506544 ) );
		interps.add( new InterpolationPoint( 0.906187624750499 , 1.013989348850028 ) );
		interps.add( new InterpolationPoint( 0.908183632734531 , 1.0258898461527237 ) );
		interps.add( new InterpolationPoint( 0.9101796407185628 , 1.0073924553492013 ) );
		interps.add( new InterpolationPoint( 0.9121756487025948 , 1.0142822427511438 ) );
		interps.add( new InterpolationPoint( 0.9141716566866267 , 0.9977791987093743 ) );
		interps.add( new InterpolationPoint( 0.9161676646706587 , 0.997145426223562 ) );
		interps.add( new InterpolationPoint( 0.9181636726546906 , 0.9679755405260848 ) );
		interps.add( new InterpolationPoint( 0.9201596806387226 , 0.9970302381204166 ) );
		interps.add( new InterpolationPoint( 0.9221556886227545 , 1.008906513005131 ) );
		interps.add( new InterpolationPoint( 0.9241516966067864 , 1.0152787149938152 ) );
		interps.add( new InterpolationPoint( 0.9261477045908184 , 1.013989348850028 ) );
		interps.add( new InterpolationPoint( 0.9281437125748503 , 1.0066944260555246 ) );
		interps.add( new InterpolationPoint( 0.9301397205588823 , 1.0072179026634402 ) );
		interps.add( new InterpolationPoint( 0.9321357285429142 , 1.0142822427511435 ) );
		interps.add( new InterpolationPoint( 0.9341317365269461 , 1.0004916763635405 ) );
		interps.add( new InterpolationPoint( 0.936127744510978 , 0.974258036009735 ) );
		interps.add( new InterpolationPoint( 0.93812375249501 , 0.963735507125337 ) );
		interps.add( new InterpolationPoint( 0.9401197604790419 , 1.0124091826878838 ) );
		interps.add( new InterpolationPoint( 0.9421157684630739 , 0.9677519165222155 ) );
		interps.add( new InterpolationPoint( 0.9441117764471058 , 1.0069852127960495 ) );
		interps.add( new InterpolationPoint( 0.9461077844311377 , 1.0141064962596429 ) );
		interps.add( new InterpolationPoint( 0.9481037924151696 , 0.9949591261493971 ) );
		interps.add( new InterpolationPoint( 0.9500998003992016 , 1.0026322175544853 ) );
		interps.add( new InterpolationPoint( 0.9520958083832335 , 1.0144580196998296 ) );
		interps.add( new InterpolationPoint( 0.9540918163672655 , 0.9959366145742279 ) );
		interps.add( new InterpolationPoint( 0.9560878243512974 , 0.9692623817155263 ) );
		interps.add( new InterpolationPoint( 0.9580838323353293 , 1.0165110029929167 ) );
		interps.add( new InterpolationPoint( 0.9600798403193613 , 0.9733580463429308 ) );
		interps.add( new InterpolationPoint( 0.9620758483033932 , 0.9626784039376971 ) );
		interps.add( new InterpolationPoint( 0.9640718562874252 , 0.9951315543669979 ) );
		interps.add( new InterpolationPoint( 0.9660678642714571 , 1.0086734330309761 ) );
		interps.add( new InterpolationPoint( 0.9680638722554891 , 1.0095477606826346 ) );
		interps.add( new InterpolationPoint( 0.9700598802395209 , 1.0258898461527237 ) );
		interps.add( new InterpolationPoint( 0.9720558882235529 , 1.0008962928162073 ) );
		interps.add( new InterpolationPoint( 0.9740518962075848 , 1.0081491999471306 ) );
		interps.add( new InterpolationPoint( 0.9760479041916168 , 0.9660763824001379 ) );
		interps.add( new InterpolationPoint( 0.9780439121756487 , 0.9972606276345275 ) );
		interps.add( new InterpolationPoint( 0.9800399201596807 , 0.9695983606764351 ) );
		interps.add( new InterpolationPoint( 0.9820359281437125 , 1.0007228657321037 ) );
		interps.add( new InterpolationPoint( 0.9840319361277445 , 0.9730769701065939 ) );
		interps.add( new InterpolationPoint( 0.9860279441117764 , 1.0142822427511438 ) );
		interps.add( new InterpolationPoint( 0.9880239520958084 , 0.9817714813020815 ) );
		interps.add( new InterpolationPoint( 0.9900199600798403 , 0.9962242938882538 ) );
		interps.add( new InterpolationPoint( 0.9920159680638723 , 1.0130526566160565 ) );
		interps.add( new InterpolationPoint( 0.9940119760479041 , 0.9705509336509293 ) );
		interps.add( new InterpolationPoint( 0.9960079840319361 , 1.0010119275693037 ) );
		interps.add( new InterpolationPoint( 0.998003992015968 , 0.9753278546233416 ) );
		interps.add( new InterpolationPoint( 1.0 , 0.9651282010927337 ) );
		return( interps );
	}
	
	/**
	 * Creates the interpolation points of the default amplitude envelope of a note parameterized over the barycentric duration of the note.
	 * @param interps The list into which to populate the interpolation points of the envelope.
	 */
	public static void createInitialEnvelope( ArrayList<InterpolationPoint> interps )
	{
		ZSoundAgentS3.createInitialEnvelope( interps );
	}
	
	@Override public void setInitialNoteEnvelope( NoteDesc nd , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		if( !( nd.isUserDefinedVibrato() ) )
		{
			nd.setVibratoParams( new VibratoParameters( vibratoParams ) );
		}
		if( !( nd.isUserDefinedNoteEnvelope() ) )
		{
			PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
			bezAC.getInterpolationPoints().clear();
			createInitialEnvelope( bezAC.getInterpolationPoints() );
			nd.getVibratoParams().buildTremolo( nd , nxt , minDecayTimeBeats , bezAC.getInterpolationPoints() , core );
			bezAC.updateAll();
			BezierCubicClampedCoefficient bezA = new BezierCubicClampedCoefficient( bezAC );
		
			nd.setNoteEnvelope( bezA );
		}
	}
	
	/**
	 * Returns an adjustment for pitch bend values.  This is an estimate based on critical listening.
	 * @param val The original pitch bend value.
	 * @return The adjusted pitch bend value.
	 */
	protected static double cval( double val )
	{
		double diff = val - 1.0;
		return( 2.8 * diff + 1.0 );
	}
	
	/**
	 * Builds the interpolation points of the pitch bend for a note.
	 * @param interps The array list into which to populate the interpolation points.
	 * @param ratio The ratio by which to affine-scale the parameter space of the interpolation points.
	 */
	protected static void buildInterpBend( ArrayList<InterpolationPoint> interps , double ratio )
	{
		interps.clear();
		InterpolationPoint prev = null;
		for( final InterpolationPoint in : getInterpBend() )
		{
			double oparam = in.getParam() * ratio;
			if( oparam > 1.0 )
			{
				double u = ( 1.0 - prev.getParam() ) / ( oparam - prev.getParam() );
				double val = (1-u) * ( prev.getValue() ) + u * ( in.getValue() );
				interps.add( new InterpolationPoint( 1.0 , cval( val ) ) );
				return;
			}
			else
			{
				interps.add( new InterpolationPoint( oparam , cval( in.getValue() ) ) );
			}
			prev = in;
		}
	}
	
	
	/**
	 * Generates the pitch bend and portamento-related amplitude changes for a note other than the last note of a track frame.
	 * @param desc The note to be processed.
	 * @param nxt The next note in the track frame.
	 * @param minDecayTimeBeats The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	public static void buildBend( NoteDesc desc , NoteDesc nxt , double minDecayTimeBeats ) throws Throwable
	{
		final int core = 0;
		double umax = ( desc.getActualEndBeatNumber() - desc.getActualStartBeatNumber() ) 
			/ ( minDecayTimeBeats );
		umax = Math.min( umax , 1.0 );
		FreqAndBend freq = desc.getFreqAndBend();
		if( !( freq.isUserDefinedBend() ) )
		{
			PiecewiseCubicMonotoneBezierFlatMultiCore bend = freq.getBendPerNoteU();
			ArrayList<InterpolationPoint> interp = bend.getInterpolationPoints();
			buildInterpBend( interp , 1.0 / umax );
			if( desc.getPortamentoDesc() != null )
			{
				desc.getPortamentoDesc().buildBend( desc , nxt ,
						desc.getFreqAndBend().getBendPerNoteU().getInterpolationPoints() , core );
			}
			desc.getVibratoParams().buildBend( desc , nxt ,
					desc.getFreqAndBend().getBendPerNoteU().getInterpolationPoints() , core );
			bend.updateAll();
			freq.setWaveInfoDirty( true );
		}
	}

	/**
	 * Produces a representation of how a musician would play an instrument for a note other than the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @param note2 The next note after the note to be processed.
	 * @throws Throwable
	 */
	protected void processFirstNote(NoteDesc note1, NoteDesc note2) throws Throwable {
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, note2.getStartBeatNumber());
		double decayTimeBeatsFinal = note1.getActualEndBeatNumber() - note1.getStartBeatNumber();
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , note2 , minDecayTimeBeats );

		double ratio = decayTimeBeatsFinal / decayTimeBeatsPlay;
		ClampedCoefficient ci = note1.getNoteEnvelope( core );
		if (ratio < 0.9999999999)
		{
			ClampedCoefficient cb = new ClampedCoefficientRemodulator(ci, ratio);
			note1.setActualNoteEnvelope(cb);
			note1.setWaveEnvelope(buildNoteAttack());
			
			/* ClampedCoefficient cb = new ClampedCoefficientRemodulator(ci, ratio);
			note1.setActualNoteEnvelope(cb);
			double endWaveNumber = note1.getEndWaveNumber();
			double startWaveNumber = endWaveNumber - cutoffTimeWaves;
			NonClampedCoefficient noteAttack = buildNoteAttack();
			NonClampedCoefficient noteDecay = buildNoteDecay(note1,
					startWaveNumber, endWaveNumber);
			NonClampedCoefficient finalEnv = new AmplitudeModulationNonClampedCoeff(
					noteAttack, noteDecay);
			note1.setWaveEnvelope(finalEnv); */
		} else {
			note1.setActualNoteEnvelope(note1.getNoteEnvelope( core ));
			note1.setWaveEnvelope(buildNoteAttack());
		}

		buildNoteInstrument(note1);
		
		if( /* SongData.ROUGH_DRAFT_MODE */ true ) // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		{
			SongData.buildBendInterpPoints(note1,note2,10,minDecayTimeBeats,false, 1.009 ,core);
		}
		else
		{
			buildBend(note1,note2,minDecayTimeBeats);
		}
		
	}

	/**
	 * Produces a representation of how a musician would play an instrument for the last note of a track frame.
	 * @param note1 The note to be processed.
	 * @throws Throwable
	 */
	protected void processLastNote(NoteDesc note1) throws Throwable {
		final int core = 0;
		double decayTimeBeats = note1.getEndBeatNumber()
				- note1.getStartBeatNumber();
		double decayTimeBeatsPlay = Math.max(minDecayTimeBeats, decayTimeBeats);
		note1.setActualEndBeatNumberValidated(note1.getStartBeatNumber()
				+ decayTimeBeatsPlay, 1E+40);
		note1.setActualStartBeatNumber(note1.getStartBeatNumber());
		setInitialNoteEnvelope( note1 , null , minDecayTimeBeats );
		note1.setActualNoteEnvelope(note1.getNoteEnvelope( core ));
		note1.setWaveEnvelope(buildNoteAttack());
		buildNoteInstrument(note1);
		
		if( /* SongData.ROUGH_DRAFT_MODE */ true ) // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		{
			SongData.buildBendInterpPoints(note1,null,10,minDecayTimeBeats,false, 1.009 ,core);
		}
		else
		{
			buildBend(note1,null,minDecayTimeBeats);
		}
		
	}

	/**
	 * Builds the final decay for a note.
	 * @param note The note for which to build the decay.
	 * @param startWaveNumber The starting wave number of the note.
	 * @param endWaveNumber The ending wave number of the note.
	 * @return The decay coefficient.
	 */
	protected NonClampedCoefficient buildNoteDecay(NoteDesc note,
			double startWaveNumber, double endWaveNumber) {
		PiecewiseCubicMonotoneBezierFlat bezDrive = SongData
				.buildGradualDecayBezier(20, startWaveNumber, endWaveNumber);
		bezDrive.updateAll();
		BezierCubicNonClampedCoefficientFlat bzC = new BezierCubicNonClampedCoefficientFlat(
				bezDrive);
		return (bzC);
	}

	/**
	 * Builds the initial attack for a note.
	 * @return The attack.
	 */
	protected NonClampedCoefficient buildNoteAttack() {
		PiecewiseCubicMonotoneBezierFlat bezDrive = SongData
				.buildGradualAttackBezier(20, 0.0, attackTimeWaves);
		bezDrive.updateAll();
		BezierCubicNonClampedCoefficientFlat bzC = new BezierCubicNonClampedCoefficientFlat(
				bezDrive);
		return (bzC);
	}
	
	/**
	 * Returns an adjustment for primary timbre coefficient values.  This is an estimate based on critical listening.
	 * @param val The original primary timbre coefficient value.
	 * @return The adjusted primary timbre coefficient value.
	 */
	double dvalPrim( double in )
	{
		return( ( in - 0.75 ) / ( 0.84 - 0.75 ) );
	}
	
	
	/**
	 * Returns an adjustment for secondary timbre coefficient values.  This is an estimate based on critical listening.
	 * @param val The original secondary timbre coefficient value.
	 * @return The adjusted secondary timbre coefficient value.
	 */
	double dvalSec( double in )
	{
		return( ( in - 0.09 ) / ( 0.25 - 0.09 ) );
	}

	/**
	 * Builds a coefficient for the primary timbre.
	 * @return The coefficient for the primary timbre.
	 */
	protected NonClampedCoefficient buildPrimaryCoeff() {
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.75 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.75 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.86 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 10.0 ,
		 * 0.79 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 13.0 , 0.75 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 15.0 , 0.75 ) );
		 */

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.75 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.75 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.82 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 17.0 ,
		 * 0.86 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 21.0 , 0.79 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 25.0 , 0.75 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 26.0 ,
		 * 0.75 ) );
		 */

		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, dvalPrim(/*0.75*/0.88)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, dvalPrim(0.75)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, dvalPrim(0.75)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(7.5, dvalPrim(0.82)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(18.0, dvalPrim(0.86)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(72.0, dvalPrim(0.82)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(86.0, dvalPrim(0.84)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(88.0, dvalPrim(0.84)));
		
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(18.0, dvalPrim(0.86)));
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(36.0, dvalPrim(0.82)));
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(52.0, dvalPrim(0.84)));
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(53.0, dvalPrim(0.84)));

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 0.0 ) );
		 * 
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 53.0 ,
		 * 0.0 ) );
		 */

		bezAC.updateAll();

		BezierCubicNonClampedCoefficientFlat coeff = new BezierCubicNonClampedCoefficientFlat(
				bezAC);

		return (coeff);
	}

	/**
	 * Builds a coefficient for the secondary timbre.
	 * @return The coefficient for the secondary timbre.
	 */
	protected NonClampedCoefficient buildSecondaryCoeff() {
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.25 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.25 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.09 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 10.0 ,
		 * 0.23 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 13.0 , 0.25 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 15.0 , 0.25 ) );
		 */

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 ,
		 * 0.25 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 5.0 , 0.25 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 7.5 , 0.15 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 17.0 ,
		 * 0.09 ) ); bezAC.getInterpolationPoints().add( new InterpolationPoint(
		 * 21.0 , 0.23 ) ); bezAC.getInterpolationPoints().add( new
		 * InterpolationPoint( 25.0 , 0.25 ) );
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 26.0 ,
		 * 0.25 ) );
		 */

		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, dvalSec(/*0.25*/0.06)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, dvalSec(0.25)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, dvalSec(0.25)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(7.5, dvalSec(0.15)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(18.0, dvalSec(0.09)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(72.0, dvalSec(0.15)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(86.0, dvalSec(0.13)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(88.0, dvalSec(0.13)));
		
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(18.0, dvalSec(0.09)));
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(36.0, dvalSec(0.15)));
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(52.0, dvalSec(0.13)));
		//bezAC.getInterpolationPoints().add(new InterpolationPoint(53.0, dvalSec(0.13)));

		/*
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 0.0 , 1.0 ) );
		 * 
		 * bezAC.getInterpolationPoints().add( new InterpolationPoint( 53.0 ,
		 * 1.0 ) );
		 */

		bezAC.updateAll();

		BezierCubicNonClampedCoefficientFlat coeff = new BezierCubicNonClampedCoefficientFlat(
				bezAC);

		return (coeff);
	}
	
	
	/**
	 * Gets the initial waveform for the secondary timbre.
	 * @return The waveform for the secondary timbre.
	 */
	protected WaveForm genWaveB( )
	{
		
		//wave3 = new SevenDistortionWaveForm( wave3 , new MultiSincWaveForm( distortionCoeff , MultiSincWaveForm.INV_MULT ) , this.genWaveB3(false) , SPLIT , topDivisor , topDivisor2 );
		// WaveForm wave3 = genWaveQB(); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		WaveForm wave3 = genWaveQA();
		
		
		return( wave3 );
	}
	
	
	/**
	 * Gets the initial waveform for the primary timbre.
	 * @return The waveform for the primary timbre.
	 */
	protected WaveForm genWaveA( )
	{
		
		// wave3 = new SawtoothWaveform();
		WaveForm wave3 = genWaveQA();
		
		return( wave3 );
	}
	
	
	/**
	 * Gets the initial waveform for the secondary timbre.
	 * @return The waveform for the secondary timbre.
	 */
	public WaveForm genWaveQB()
	{
		double m2 = 1.0;
		double maxDivisor = 4.2;
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		PhaseDistortionPacket pdcxf2 = new PhaseDistortionPacket(
				new SineWaveform(), 1.0,  0.048791979949874685 );
		PhaseDistortionPacket pdcxg2 = new PhaseDistortionPacket(
				new SineWaveform(), 1.0,  0.04013533834586466 );
		PhaseDistortionPacket pdcxh2 = new PhaseDistortionPacket(
				new SineWaveform(), 1.0,  0.035020050125313285 );
		PhaseDistortionPacket pdcxi2 = new PhaseDistortionPacket(
				new SineWaveform(), 1.0,  0.009050125313283208 );
		PhaseDistortionPacket[] pdccxf2 = { pdcxf2 };
		PhaseDistortionPacket[] pdccxg2 = { pdcxg2 };
		PhaseDistortionPacket[] pdccxh2 = { pdcxh2 };
		PhaseDistortionPacket[] pdccxi2 = { pdcxi2 };
		WaveForm invf2 = new ArithGntWaveform( new Inverter(pdccxf2) , 4 );
		WaveForm invg2 = new ArithGntWaveform( new Inverter(pdccxg2) , 4 );
		WaveForm invh2 = new ArithGntWaveform( new Inverter(pdccxh2) , 4 );
		WaveForm invi2 = new ArithGntWaveform( new Inverter(pdccxi2) , 3 );
		coefficients.add( invf2 );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 500.0 * ( 642.2032455533998 / -2.9098130489917816E-5 ) / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invg2 );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 500.0 * ( 361.19249433225104 / -2.9098130489917816E-5 ) / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invh2 );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 500.0 * ( 60.833800291526266 / -2.9098130489917816E-5 ) / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invi2 );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 500.0 * ( -58.3125629856042 / -2.9098130489917816E-5 ) / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		
		
		// WaveForm wave3 = MultiXtalNativeFluteP0.builtMultiXtalNativeFluteP0B1(); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		WaveForm wave3 = MultiXtalNativeFluteP0.builtMultiXtalNativeFluteP0A1();
		//wave3 = new AdditiveWaveForm( wave3 , new ConstantNonClampedCoefficient( 1.0 / maxDivisor ) ,
		//		coefficients , coefficientCoefficients , parameterCoefficients );
		
		System.out.println( "Got New Built19A!!!" );
		
		// wave3 = new HarmonicDistortionWaveForm( wave3 , 0.5 , -1 , 0.03 , 10 , false , true , false );
		
		/* final double topDivisor = 10;
		final double topDivisor2 = 10 * 330;
		
		wave3 = new SpreadWaveform( wave3 , (int) topDivisor , ( (int) topDivisor2 ) ); */
		
		return( wave3 );
	}
	
	
	/**
	 * Gets the initial waveform for the primary timbre.
	 * @return The waveform for the primary timbre.
	 */
	public WaveForm genWaveQA()
	{	
		double m2 = 1.0;
		double maxDivisor = 1.0;
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		PhaseDistortionPacket pdcxf2 = new PhaseDistortionPacket(
				new SineWaveform(), 1.0,  0.059416040100250624 );
		PhaseDistortionPacket pdcxg2 = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.03344611528822055 );
		//PhaseDistortionPacket pdcxh2 = new PhaseDistortionPacket(
		//		new SineWaveform(), 1.0,  0.031872180451127816 );
		PhaseDistortionPacket[] pdccxf2 = { pdcxf2 };
		PhaseDistortionPacket[] pdccxg2 = { pdcxg2 };
		//PhaseDistortionPacket[] pdccxh2 = { pdcxh2 };
		WaveForm invf2 = new ArithGntWaveform( new Inverter(pdccxf2) , 3 );
		WaveForm invg2 = new ArithGntWaveform( new Inverter(pdccxg2) , 4 );
		//WaveForm invh2 = new ArithGntWaveform( new Inverter(pdccxh2) , 4 );
		coefficients.add( invf2 );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.05 * ( 1228460.0021170813 / 1.312 ) / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invg2 );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 10 * 0.05 * ( 7366.999558857282 / 1.312 ) / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		
		coefficients.add( MultiXtalNativeFluteP0.builtMultiXtalNativeFluteP0A1() );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 1.25 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 2.0 ));
		
		coefficients.add( MultiXtalNativeFluteP0.builtMultiXtalNativeFluteP0A1() );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.01 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 0.5 ));
		
		coefficients.add( MultiXtalNativeFluteP0.builtMultiXtalNativeFluteP0A1() );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02 * 0.02 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 0.25 ));
		
		WaveForm wave3 = MultiXtalNativeFluteP0.builtMultiXtalNativeFluteP0A1();
		wave3 = new AdditiveWaveForm( wave3 , new ConstantNonClampedCoefficient( 1.0 / maxDivisor ) ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
		// wave3 = new HarmonicDistortionWaveForm( wave3 , 0.5 , -1 , 0.03 , 10 , false , true , false ); !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		// System.out.println( "Got New Built8C!!!" );
		
		/* final double topDivisor = 10;
		final double topDivisor2 = 10 * 330;
		
		wave3 = new SpreadWaveform( wave3 , (int) topDivisor , ( (int) topDivisor2 ) ); */
		
		return( wave3 );
	}
	

	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note) throws Throwable
	{
		currentNote = note;
		buildNoteInstrument(note, false);
		if( SongData.roughDraftMode == SongData.ROUGH_DRAFT_MODE_BEZ_APPROX )
		{
			buildNoteInstrument( note , true );
		}
	}

	/**
	 * Represents how a musician would play an instrument for a particular note.
	 * @param note The note for which to build the representation.
	 * @param useRoughDraft Whether the timbre should be constructed as a rough draft.
	 * @throws Throwable
	 */
	protected void buildNoteInstrument(NoteDesc note, boolean useRoughDraft) throws Throwable {
		
		final int core = 0;
		
		WaveForm inva = getEditPack2().processWave( new SineWaveform() );
		WaveForm invb = getEditPack3().processWave( new SineWaveform() );
		

		// WaveForm wave3 = buildOverdrive( buildOverdrive( inv ) );

		/*
		 * if( useInfraSonicHighPass() ) { inva = new HighPassFilter( inva ,
		 * 23.4 , 25 ); invb = new HighPassFilter( invb , 23.4 , 25 ); }
		 */

		WaveForm wave3a = inva;
		WaveForm wave3b = invb;

		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();

		NonClampedCoefficient primaryCoeff = buildPrimaryCoeff();

		NonClampedCoefficient secondaryCoeff = buildSecondaryCoeff();

		coefficients.add(wave3b);
		coefficientCoefficients.add(secondaryCoeff);
		parameterCoefficients.add(new ConstantNonClampedCoefficient(1.0));

		WaveForm wave3 = new AdditiveWaveForm(wave3a, primaryCoeff,
				coefficients, coefficientCoefficients, parameterCoefficients);
		
		wave3 = editPack1.processWave( wave3 );

		
		note.setWaveform(wave3);
		
		
		// wave3 = genWaveB( ); 
	
		//wave3 = genWaveB( false ); //
		
		// wave3 = genWaveA3(false);
		
		//wave3 = new SpreadWaveform( /* new SquareWaveform(0.25) */ new SineWaveform() , (int) maxHarmonicNum , 330 * ( (int) maxHarmonicNum ) );
		//WaveForm wave32 = new SpreadWaveform( /* new SquareWaveform(0.25) */ new SineWaveform() , (int) ( maxHarmonicNum / 2.0 ) , 330 * ( (int) ( maxHarmonicNum / 2.0 ) ) );
		//wave3 = new SevenDistortionWaveForm( wave3 , wave32 ,
		//		1.0 , 1 , 
		//		1.0 , 1 ,
		//		true , true , true );
		
		wave3 = editPack1.processWave( wave3 );
		
		// wave3 = genWaveB3(false);
		
		note.setWaveform( wave3 ); //
		
		
//		note.setRoughDraftWaveform(new SineWaveform());
		
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}

	/**
	 * Puts a phase distortion for the initial instrument "pluck" (or equivalent thereof) on top of the default timbre.
	 * @param wave0 The input default timbre
	 * @return The phase-distorted version of the timbre
	 */
	public static GWaveForm buildBassWaveFormB(GWaveForm wave0) {
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		final double strt = 8;
		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, -8+strt));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(1.0, -4+strt));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, -2+strt));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(3.0, -1+strt));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(4.0, -0.5+strt));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, 0.0+strt));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(6.0, 0.0+strt));
		bezAC.updateAll();

		GAnalogPhaseDistortionWaveForm wave = new GAnalogPhaseDistortionWaveForm(
				wave0, bezAC.genBez(new HashMap()));

		return (wave);
	}

	/**
	 * Produces a representation of how a musician would play an instrument on a particular track frame.
	 * @param tr The track frame.
	 * @throws Throwable
	 */
	protected void processTrackFrame(TrackFrame tr) throws Throwable {
		NoteDesc note1 = null;
		NoteDesc note2 = null;

		ArrayList<NoteDesc> notes = tr.getNotes();
		if (notes.size() > 0) {
			for( final NoteDesc note : notes ) {
				note1 = note2;
				note2 = note;
				if (note1 != null) {
					processFirstNote(note1, note2);
				}
			}

			if (note2 != null) {
				processLastNote(note2);
			} else {
				processLastNote(note1);
			}
		}
	}

	/**
	 * Constructs the agent.  This is usually invoked by introspection.
	 */
	public NativeFluteAgentP0() {
		super();
		
		WaveForm inva = genWaveB();
		WaveForm invb = genWaveA();
		
		GWaveForm ina = new GRoughDraftWaveSwitch( inva.genWave( new HashMap() ) , new GSineWaveform() );
		GWaveForm inb = new GRoughDraftWaveSwitch( invb.genWave( new HashMap() ) , new GSineWaveform() );
		
		ina = new GAppxBezWaveform( ina , "A" , getClass() );
		inb = new GAppxBezWaveform( inb , "B" , getClass() );
		
		ina = buildBassWaveFormB( ina );
		inb = buildBassWaveFormB( inb );
		
		getEditPack2().getWaveOut().performAssign( ina );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack2().getElem(), null, getEditPack2().getWaveOut());
		
		getEditPack3().getWaveOut().performAssign( inb );
		AazonTransChld.initialCoords(AczonUnivAllocator.allocateUniv(),getEditPack3().getElem(), null, getEditPack3().getWaveOut());
		
		applyStdAmplRoll( getEditPack1().getWaveIn() , getEditPack1().getWaveOut() , getEditPack1() );
	}
	
	@Override
	public void initializeInitializers()
	{
		NoteInitializer fa = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(4,
				NoteTable.STEPS_A) );
		NoteInitializer fb = new NoteInitializer( NoteTable.getCloseNoteDefaultScale_Key(4,
				NoteTable.STEPS_B) );
		noteInitializers.clear();
		noteInitializers.add( fa );
		noteInitializers.add( fb );
	}

	@Override
	public void processTrack(InstrumentTrack track) throws Throwable {
		final int core = 0;

		track.updateTrackFramesComp( core );

		ArrayList<TrackFrame> trackFrames = track.getTrackFrames();
		for( final TrackFrame tr : trackFrames ) {
			processTrackFrame(tr);
		}

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		VersionBuffer myv = new VersionBuffer(VersionBuffer.WRITE);

		myv.setDouble("MinDecayTimeBeats", minDecayTimeBeats);
		myv.setDouble("CutoffTimeWaves", cutoffTimeWaves);
		myv.setDouble("AttackTimeWaves", attackTimeWaves);

		out.writeObject(myv);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		try {
			super.readExternal(in);
			VersionBuffer myv = (VersionBuffer) (in.readObject());
			VersionBuffer.chkNul(myv);

			minDecayTimeBeats = myv.getDouble("MinDecayTimeBeats");
			cutoffTimeWaves = myv.getDouble("CutoffTimeWaves");
			attackTimeWaves = myv.getDouble("AttackTimeWaves");

		} catch (ClassCastException ex) {
			throw (new DataFormatException(ex));
		}
	}

}
