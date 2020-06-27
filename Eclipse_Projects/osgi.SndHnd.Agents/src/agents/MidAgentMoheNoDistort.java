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
import waves.GAppxBezWaveform;
import waves.GRoughDraftWaveSwitch;
import waves.GSawtoothWaveform;
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
import core.PhaseDistortionPacket;
import core.SongData;
import core.TrackFrame;
import core.VibratoParameters;
import core.WaveForm;
import cwaves.AdditiveWaveForm;
import cwaves.ClampedCoefficientRemodulator;
import cwaves.ConstantNonClampedCoefficient;
import cwaves.GAnalogPhaseDistortionWaveForm;
import cwaves.Inverter;
import cwaves.SineWaveform;
import cwaves.SinglePacketInverter;

/**
 * 
 * Similar to MidAgentMohe but without any built-in distortion of the tone.
 * 
 * Created for use in a song about the Independent Lens feature "How Is Your Fish Today?" that was never finished.
 * 
 * @author tgreen
 *
 */
public class MidAgentMoheNoDistort extends IntelligentAgent implements Externalizable {
	
	/**
	 * Stores each interpolated rough-draft waveform under a unique name.
	 */
	public static final HashMap<String,WaveForm> roughDraftWaveformMap = new HashMap<String,WaveForm>();

	/**
	 * The minimum number of beats over which the instrument amplitude can decay without introducing an interruption.
	 */
	protected double minDecayTimeBeats = 5.0;

	/**
	 * For an instrument amplitude decay that is interrupted before minDecayTimeBeats, the number of waves over which the interruption of the amplitude takes place.
	 */
	protected double cutoffTimeWaves = 2.5;

	/**
	 * The duration of the initial attack of a note in waves.
	 */
	protected double attackTimeWaves = 3.0 /* 5.0 */;
	
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
		interps.add( new InterpolationPoint( 0.0 , 1.0025548012987395 ) );
		interps.add( new InterpolationPoint( 0.001996007984031936 , 1.0079549074652039 ) );
		interps.add( new InterpolationPoint( 0.003992015968063872 , 1.0022074024849053 ) );
		interps.add( new InterpolationPoint( 0.005988023952095809 , 1.0094115028782153 ) );
		interps.add( new InterpolationPoint( 0.007984031936127744 , 1.0084790395178262 ) );
		interps.add( new InterpolationPoint( 0.00998003992015968 , 1.014203926961836 ) );
		interps.add( new InterpolationPoint( 0.011976047904191617 , 1.0085372932338177 ) );
		interps.add( new InterpolationPoint( 0.013972055888223553 , 0.9918412269777808 ) );
		interps.add( new InterpolationPoint( 0.015968063872255488 , 1.0108702032169343 ) );
		interps.add( new InterpolationPoint( 0.017964071856287425 , 1.0027864674054525 ) );
		interps.add( new InterpolationPoint( 0.01996007984031936 , 1.0068493008565433 ) );
		interps.add( new InterpolationPoint( 0.021956087824351298 , 1.005338330441863 ) );
		interps.add( new InterpolationPoint( 0.023952095808383235 , 1.0029023205326937 ) );
		interps.add( new InterpolationPoint( 0.02594810379241517 , 0.9959172400803602 ) );
		interps.add( new InterpolationPoint( 0.027944111776447105 , 0.9989708128855036 ) );
		interps.add( new InterpolationPoint( 0.029940119760479042 , 1.0097613988756915 ) );
		interps.add( new InterpolationPoint( 0.031936127744510975 , 1.0027864674054525 ) );
		interps.add( new InterpolationPoint( 0.033932135728542916 , 1.0020916296331082 ) );
		interps.add( new InterpolationPoint( 0.03592814371257485 , 1.0073146715411465 ) );
		interps.add( new InterpolationPoint( 0.03792415169660679 , 1.0041195867627564 ) );
		interps.add( new InterpolationPoint( 0.03992015968063872 , 1.0022074024849053 ) );
		interps.add( new InterpolationPoint( 0.041916167664670656 , 1.0026706276613164 ) );
		interps.add( new InterpolationPoint( 0.043912175648702596 , 0.9977597883707204 ) );
		interps.add( new InterpolationPoint( 0.04590818363273453 , 1.0044096297531362 ) );
		interps.add( new InterpolationPoint( 0.04790419161676647 , 1.0059192043797296 ) );
		interps.add( new InterpolationPoint( 0.0499001996007984 , 1.002728545860593 ) );
		interps.add( new InterpolationPoint( 0.05189620758483034 , 1.0033079118923143 ) );
		interps.add( new InterpolationPoint( 0.05389221556886228 , 1.0001255360887122 ) );
		interps.add( new InterpolationPoint( 0.05588822355289421 , 1.0033079118923143 ) );
		interps.add( new InterpolationPoint( 0.05788423153692615 , 1.006267889829017 ) );
		interps.add( new InterpolationPoint( 0.059880239520958084 , 0.9120476460425355 ) );
		interps.add( new InterpolationPoint( 0.06187624750499002 , 0.9944801170839376 ) );
		interps.add( new InterpolationPoint( 0.06387225548902195 , 0.9994325420943293 ) );
		interps.add( new InterpolationPoint( 0.0658682634730539 , 1.008420789166604 ) );
		interps.add( new InterpolationPoint( 0.06786427145708583 , 0.999605745563011 ) );
		interps.add( new InterpolationPoint( 0.06986027944111776 , 0.9995480077385434 ) );
		interps.add( new InterpolationPoint( 0.0718562874251497 , 1.0043516144534506 ) );
		interps.add( new InterpolationPoint( 0.07385229540918163 , 1.0029023205326937 ) );
		interps.add( new InterpolationPoint( 0.07584830339321358 , 0.9957446757255959 ) );
		interps.add( new InterpolationPoint( 0.07784431137724551 , 1.0101697641673828 ) );
		interps.add( new InterpolationPoint( 0.07984031936127745 , 0.9962049137980048 ) );
		interps.add( new InterpolationPoint( 0.08183632734530938 , 1.000876821839035 ) );
		interps.add( new InterpolationPoint( 0.08383233532934131 , 0.9909822342242184 ) );
		interps.add( new InterpolationPoint( 0.08582834331337326 , 1.0102864702957834 ) );
		interps.add( new InterpolationPoint( 0.08782435129740519 , 1.0034817869695971 ) );
		interps.add( new InterpolationPoint( 0.08982035928143713 , 1.0083625421799565 ) );
		interps.add( new InterpolationPoint( 0.09181636726546906 , 1.0003566408511355 ) );
		interps.add( new InterpolationPoint( 0.09381237524950099 , 1.0011659281469238 ) );
		interps.add( new InterpolationPoint( 0.09580838323353294 , 1.0060354194358432 ) );
		interps.add( new InterpolationPoint( 0.09780439121756487 , 0.998163299999774 ) );
		interps.add( new InterpolationPoint( 0.0998003992015968 , 1.0031920119120283 ) );
		interps.add( new InterpolationPoint( 0.10179640718562874 , 1.0004144253854321 ) );
		interps.add( new InterpolationPoint( 0.10379241516966067 , 0.996953254410389 ) );
		interps.add( new InterpolationPoint( 0.10578842315369262 , 0.9898380676785851 ) );
		interps.add( new InterpolationPoint( 0.10778443113772455 , 1.0011081002053792 ) );
		interps.add( new InterpolationPoint( 0.10978043912175649 , 0.9993170897884589 ) );
		interps.add( new InterpolationPoint( 0.11177644710578842 , 0.9992593686369211 ) );
		interps.add( new InterpolationPoint( 0.11377245508982035 , 0.9938484401190234 ) );
		interps.add( new InterpolationPoint( 0.1157684630738523 , 1.0044096297531362 ) );
		interps.add( new InterpolationPoint( 0.11776447105788423 , 0.9970684336195135 ) );
		interps.add( new InterpolationPoint( 0.11976047904191617 , 1.0090034441167761 ) );
		interps.add( new InterpolationPoint( 0.1217564870259481 , 1.0008768218390351 ) );
		interps.add( new InterpolationPoint( 0.12375249500998003 , 0.991096723594196 ) );
		interps.add( new InterpolationPoint( 0.12574850299401197 , 1.001165928146924 ) );
		interps.add( new InterpolationPoint( 0.1277445109780439 , 0.9985092969915873 ) );
		interps.add( new InterpolationPoint( 0.12974051896207583 , 0.996953254410389 ) );
		interps.add( new InterpolationPoint( 0.1317365269461078 , 0.9963200065504381 ) );
		interps.add( new InterpolationPoint( 0.13373253493013973 , 0.9959172400803602 ) );
		interps.add( new InterpolationPoint( 0.13572854291417166 , 1.0059192043797298 ) );
		interps.add( new InterpolationPoint( 0.1377245508982036 , 0.9980479943140311 ) );
		interps.add( new InterpolationPoint( 0.13972055888223553 , 1.000992454342626 ) );
		interps.add( new InterpolationPoint( 0.14171656686626746 , 1.001512965950703 ) );
		interps.add( new InterpolationPoint( 0.1437125748502994 , 0.9986823404602216 ) );
		interps.add( new InterpolationPoint( 0.14570858283433133 , 1.006674842284129 ) );
		interps.add( new InterpolationPoint( 0.14770459081836326 , 1.0057449069667892 ) );
		interps.add( new InterpolationPoint( 0.1497005988023952 , 1.0026127128074296 ) );
		interps.add( new InterpolationPoint( 0.15169660678642716 , 0.9990285173689261 ) );
		interps.add( new InterpolationPoint( 0.1536926147704591 , 1.0032499602285099 ) );
		interps.add( new InterpolationPoint( 0.15568862275449102 , 1.002149514387181 ) );
		interps.add( new InterpolationPoint( 0.15768463073852296 , 0.9992593686369211 ) );
		interps.add( new InterpolationPoint( 0.1596806387225549 , 1.0019758701551427 ) );
		interps.add( new InterpolationPoint( 0.16167664670658682 , 0.9964926706110757 ) );
		interps.add( new InterpolationPoint( 0.16367265469061876 , 0.9941355162575198 ) );
		interps.add( new InterpolationPoint( 0.1656686626746507 , 0.9963200065504378 ) );
		interps.add( new InterpolationPoint( 0.16766467065868262 , 0.9963200065504381 ) );
		interps.add( new InterpolationPoint( 0.16966067864271456 , 1.001686529960605 ) );
		interps.add( new InterpolationPoint( 0.17165668662674652 , 1.0022074024849053 ) );
		interps.add( new InterpolationPoint( 0.17365269461077845 , 1.0034238252626646 ) );
		interps.add( new InterpolationPoint( 0.17564870259481039 , 0.9966077966083999 ) );
		interps.add( new InterpolationPoint( 0.17764471057884232 , 0.9967805105435757 ) );
		interps.add( new InterpolationPoint( 0.17964071856287425 , 1.0018022560107496 ) );
		interps.add( new InterpolationPoint( 0.18163672654690619 , 1.0054544783889114 ) );
		interps.add( new InterpolationPoint( 0.18363273453093812 , 1.0053964027381452 ) );
		interps.add( new InterpolationPoint( 0.18562874251497005 , 0.997414051093793 ) );
		interps.add( new InterpolationPoint( 0.18762475049900199 , 1.0025548012987395 ) );
		interps.add( new InterpolationPoint( 0.18962075848303392 , 1.0019758701551427 ) );
		interps.add( new InterpolationPoint( 0.19161676646706588 , 0.9965502319472532 ) );
		interps.add( new InterpolationPoint( 0.1936127744510978 , 1.006674842284129 ) );
		interps.add( new InterpolationPoint( 0.19560878243512975 , 1.0034817869695971 ) );
		interps.add( new InterpolationPoint( 0.19760479041916168 , 0.9911539732392498 ) );
		interps.add( new InterpolationPoint( 0.1996007984031936 , 0.9983939513370133 ) );
		interps.add( new InterpolationPoint( 0.20159680638722555 , 0.9977021571698418 ) );
		interps.add( new InterpolationPoint( 0.20359281437125748 , 0.9980479943140311 ) );
		interps.add( new InterpolationPoint( 0.2055888223552894 , 0.9968956697953936 ) );
		interps.add( new InterpolationPoint( 0.20758483033932135 , 1.000703398128702 ) );
		interps.add( new InterpolationPoint( 0.20958083832335328 , 0.9970108423516983 ) );
		interps.add( new InterpolationPoint( 0.21157684630738524 , 0.9935040581772273 ) );
		interps.add( new InterpolationPoint( 0.21357285429141717 , 0.9968956697953936 ) );
		interps.add( new InterpolationPoint( 0.2155688622754491 , 0.9992593686369211 ) );
		interps.add( new InterpolationPoint( 0.21756487025948104 , 1.0034238252626646 ) );
		interps.add( new InterpolationPoint( 0.21956087824351297 , 1.013676819540476 ) );
		interps.add( new InterpolationPoint( 0.2215568862275449 , 1.0047577919330548 ) );
		interps.add( new InterpolationPoint( 0.22355289421157684 , 1.002728545860593 ) );
		interps.add( new InterpolationPoint( 0.22554890219560877 , 0.9929303534382706 ) );
		interps.add( new InterpolationPoint( 0.2275449101796407 , 0.9935040581772273 ) );
		interps.add( new InterpolationPoint( 0.22954091816367264 , 0.9967229359063691 ) );
		interps.add( new InterpolationPoint( 0.2315369261477046 , 0.9994325420943293 ) );
		interps.add( new InterpolationPoint( 0.23353293413173654 , 1.0027864674054525 ) );
		interps.add( new InterpolationPoint( 0.23552894211576847 , 0.9999522425550001 ) );
		interps.add( new InterpolationPoint( 0.2375249500998004 , 1.0055125573943557 ) );
		interps.add( new InterpolationPoint( 0.23952095808383234 , 0.9999522425550001 ) );
		interps.add( new InterpolationPoint( 0.24151696606786427 , 0.9874396160645116 ) );
		interps.add( new InterpolationPoint( 0.2435129740518962 , 0.9851607797404107 ) );
		interps.add( new InterpolationPoint( 0.24550898203592814 , 1.0020916296331082 ) );
		interps.add( new InterpolationPoint( 0.24750499001996007 , 0.9997212312176301 ) );
		interps.add( new InterpolationPoint( 0.249500998003992 , 1.0111037908114167 ) );
		interps.add( new InterpolationPoint( 0.25149700598802394 , 1.0044096297531362 ) );
		interps.add( new InterpolationPoint( 0.25349301397205587 , 1.0113374323823279 ) );
		interps.add( new InterpolationPoint( 0.2554890219560878 , 1.005048019270935 ) );
		interps.add( new InterpolationPoint( 0.25748502994011974 , 0.9998367302144462 ) );
		interps.add( new InterpolationPoint( 0.25948103792415167 , 1.0028443922960883 ) );
		interps.add( new InterpolationPoint( 0.26147704590818366 , 1.0005877990162657 ) );
		interps.add( new InterpolationPoint( 0.2634730538922156 , 1.0044096297531362 ) );
		interps.add( new InterpolationPoint( 0.2654690618762475 , 0.9980479943140311 ) );
		interps.add( new InterpolationPoint( 0.26746506986027946 , 0.9975869047543304 ) );
		interps.add( new InterpolationPoint( 0.2694610778443114 , 0.9992016508193915 ) );
		interps.add( new InterpolationPoint( 0.2714570858283433 , 1.0023231887120794 ) );
		interps.add( new InterpolationPoint( 0.27345309381237526 , 0.9997212312176301 ) );
		interps.add( new InterpolationPoint( 0.2754491017964072 , 1.0054544783889114 ) );
		interps.add( new InterpolationPoint( 0.2774451097804391 , 0.9958597153061708 ) );
		interps.add( new InterpolationPoint( 0.27944111776447106 , 0.9853884265104759 ) );
		interps.add( new InterpolationPoint( 0.281437125748503 , 1.0023231887120791 ) );
		interps.add( new InterpolationPoint( 0.2834331337325349 , 1.004235593906881 ) );
		interps.add( new InterpolationPoint( 0.28542914171656686 , 1.0022652939264736 ) );
		interps.add( new InterpolationPoint( 0.2874251497005988 , 0.9983939513370133 ) );
		interps.add( new InterpolationPoint( 0.2894211576846307 , 1.001512965950703 ) );
		interps.add( new InterpolationPoint( 0.29141716566866266 , 1.0031340669426763 ) );
		interps.add( new InterpolationPoint( 0.2934131736526946 , 1.0033079118923143 ) );
		interps.add( new InterpolationPoint( 0.2954091816367265 , 0.9940206758527246 ) );
		interps.add( new InterpolationPoint( 0.29740518962075846 , 1.0037136672802232 ) );
		interps.add( new InterpolationPoint( 0.2994011976047904 , 1.002960252115462 ) );
		interps.add( new InterpolationPoint( 0.3013972055888224 , 0.9968956697953936 ) );
		interps.add( new InterpolationPoint( 0.3033932135728543 , 1.0005877990162657 ) );
		interps.add( new InterpolationPoint( 0.30538922155688625 , 0.9974716656525313 ) );
		interps.add( new InterpolationPoint( 0.3073852295409182 , 0.9915548133603352 ) );
		interps.add( new InterpolationPoint( 0.3093812375249501 , 0.9985092969915875 ) );
		interps.add( new InterpolationPoint( 0.31137724550898205 , 0.9989131117351263 ) );
		interps.add( new InterpolationPoint( 0.313373253493014 , 1.001917995430864 ) );
		interps.add( new InterpolationPoint( 0.3153692614770459 , 1.0000100037296609 ) );
		interps.add( new InterpolationPoint( 0.31736526946107785 , 1.00906172812444 ) );
		interps.add( new InterpolationPoint( 0.3193612774451098 , 1.0011081002053792 ) );
		interps.add( new InterpolationPoint( 0.3213572854291417 , 1.0031920119120283 ) );
		interps.add( new InterpolationPoint( 0.32335329341317365 , 0.9856730589483634 ) );
		interps.add( new InterpolationPoint( 0.3253493013972056 , 0.9992593686369211 ) );
		interps.add( new InterpolationPoint( 0.3273453093812375 , 1.0034817869695971 ) );
		interps.add( new InterpolationPoint( 0.32934131736526945 , 1.0033658669036347 ) );
		interps.add( new InterpolationPoint( 0.3313373253493014 , 0.9970684336195137 ) );
		interps.add( new InterpolationPoint( 0.3333333333333333 , 1.001686529960605 ) );
		interps.add( new InterpolationPoint( 0.33532934131736525 , 0.9987400282803424 ) );
		interps.add( new InterpolationPoint( 0.3373253493013972 , 1.0037136672802232 ) );
		interps.add( new InterpolationPoint( 0.3393213572854291 , 1.0074892409971294 ) );
		interps.add( new InterpolationPoint( 0.3413173652694611 , 1.0045256704062764 ) );
		interps.add( new InterpolationPoint( 0.34331337325349304 , 1.000298859654508 ) );
		interps.add( new InterpolationPoint( 0.34530938123752497 , 1.002728545860593 ) );
		interps.add( new InterpolationPoint( 0.3473053892215569 , 1.0011081002053792 ) );
		interps.add( new InterpolationPoint( 0.34930139720558884 , 1.0083625421799565 ) );
		interps.add( new InterpolationPoint( 0.35129740518962077 , 0.997702157169842 ) );
		interps.add( new InterpolationPoint( 0.3532934131736527 , 0.9913257420169632 ) );
		interps.add( new InterpolationPoint( 0.35528942115768464 , 1.0001833072734894 ) );
		interps.add( new InterpolationPoint( 0.35728542914171657 , 1.0040615882161272 ) );
		interps.add( new InterpolationPoint( 0.3592814371257485 , 1.009353198667353 ) );
		interps.add( new InterpolationPoint( 0.36127744510978044 , 0.9949397706711407 ) );
		interps.add( new InterpolationPoint( 0.36327345309381237 , 0.9963200065504381 ) );
		interps.add( new InterpolationPoint( 0.3652694610778443 , 1.008945163475631 ) );
		interps.add( new InterpolationPoint( 0.36726546906187624 , 1.0081878214055218 ) );
		interps.add( new InterpolationPoint( 0.36926147704590817 , 1.0100530715206244 ) );
		interps.add( new InterpolationPoint( 0.3712574850299401 , 1.0023231887120791 ) );
		interps.add( new InterpolationPoint( 0.37325349301397204 , 1.015728221899858 ) );
		interps.add( new InterpolationPoint( 0.37524950099800397 , 1.0063260158203056 ) );
		interps.add( new InterpolationPoint( 0.3772455089820359 , 0.9970684336195137 ) );
		interps.add( new InterpolationPoint( 0.37924151696606784 , 0.9916693688810696 ) );
		interps.add( new InterpolationPoint( 0.3812375249500998 , 1.0031920119120283 ) );
		interps.add( new InterpolationPoint( 0.38323353293413176 , 0.99442267531883 ) );
		interps.add( new InterpolationPoint( 0.3852295409181637 , 0.9992593686369211 ) );
		interps.add( new InterpolationPoint( 0.3872255489021956 , 0.9992016508193915 ) );
		interps.add( new InterpolationPoint( 0.38922155688622756 , 1.0019179954308641 ) );
		interps.add( new InterpolationPoint( 0.3912175648702595 , 0.9993748142741973 ) );
		interps.add( new InterpolationPoint( 0.3932135728542914 , 0.9977597883707204 ) );
		interps.add( new InterpolationPoint( 0.39520958083832336 , 0.9854453464218377 ) );
		interps.add( new InterpolationPoint( 0.3972055888223553 , 1.0014551179640474 ) );
		interps.add( new InterpolationPoint( 0.3992015968063872 , 1.0051641336779849 ) );
		interps.add( new InterpolationPoint( 0.40119760479041916 , 0.9911539732392498 ) );
		interps.add( new InterpolationPoint( 0.4031936127744511 , 1.007838470673946 ) );
		interps.add( new InterpolationPoint( 0.405189620758483 , 1.0032499602285099 ) );
		interps.add( new InterpolationPoint( 0.40718562874251496 , 0.9981632999997743 ) );
		interps.add( new InterpolationPoint( 0.4091816367265469 , 1.0067911446399989 ) );
		interps.add( new InterpolationPoint( 0.4111776447105788 , 0.9506686113664666 ) );
		interps.add( new InterpolationPoint( 0.41317365269461076 , 0.9997789790481674 ) );
		interps.add( new InterpolationPoint( 0.4151696606786427 , 1.0085372932338177 ) );
		interps.add( new InterpolationPoint( 0.4171656686626746 , 1.0020916296331082 ) );
		interps.add( new InterpolationPoint( 0.41916167664670656 , 0.994824837360599 ) );
		interps.add( new InterpolationPoint( 0.42115768463073855 , 0.9961473724076096 ) );
		interps.add( new InterpolationPoint( 0.4231536926147705 , 0.9945375621671073 ) );
		interps.add( new InterpolationPoint( 0.4251497005988024 , 0.9965502319472532 ) );
		interps.add( new InterpolationPoint( 0.42714570858283435 , 0.9963775579128602 ) );
		interps.add( new InterpolationPoint( 0.4291417165668663 , 0.9990862251855865 ) );
		interps.add( new InterpolationPoint( 0.4311377245508982 , 0.9976445292977763 ) );
		interps.add( new InterpolationPoint( 0.43313373253493015 , 0.9933892907171166 ) );
		interps.add( new InterpolationPoint( 0.4351297405189621 , 0.9958021938546464 ) );
		interps.add( new InterpolationPoint( 0.437125748502994 , 1.0087120745723532 ) );
		interps.add( new InterpolationPoint( 0.43912175648702595 , 0.9896093929418693 ) );
		interps.add( new InterpolationPoint( 0.4411177644710579 , 1.0081878214055215 ) );
		interps.add( new InterpolationPoint( 0.4431137724550898 , 0.9906388454615751 ) );
		interps.add( new InterpolationPoint( 0.44510978043912175 , 1.0081295878752312 ) );
		interps.add( new InterpolationPoint( 0.4471057884231537 , 1.0040035930195284 ) );
		interps.add( new InterpolationPoint( 0.4491017964071856 , 0.9979903464662053 ) );
		interps.add( new InterpolationPoint( 0.45109780439121755 , 0.9979903464662053 ) );
		interps.add( new InterpolationPoint( 0.4530938123752495 , 0.999605745563011 ) );
		interps.add( new InterpolationPoint( 0.4550898203592814 , 0.999605745563011 ) );
		interps.add( new InterpolationPoint( 0.45708582834331335 , 1.0024968931350524 ) );
		interps.add( new InterpolationPoint( 0.4590818363273453 , 0.9986246559721841 ) );
		interps.add( new InterpolationPoint( 0.46107784431137727 , 1.0045836957601186 ) );
		interps.add( new InterpolationPoint( 0.4630738522954092 , 0.9850469760779488 ) );
		interps.add( new InterpolationPoint( 0.46506986027944114 , 0.9956296494341469 ) );
		interps.add( new InterpolationPoint( 0.46706586826347307 , 0.9985092969915877 ) );
		interps.add( new InterpolationPoint( 0.469061876247505 , 0.9959172400803602 ) );
		interps.add( new InterpolationPoint( 0.47105788423153694 , 0.996953254410389 ) );
		interps.add( new InterpolationPoint( 0.47305389221556887 , 0.9945375621671073 ) );
		interps.add( new InterpolationPoint( 0.4750499001996008 , 1.0005300044678043 ) );
		interps.add( new InterpolationPoint( 0.47704590818363274 , 1.0005877990162657 ) );
		interps.add( new InterpolationPoint( 0.47904191616766467 , 0.9963200065504381 ) );
		interps.add( new InterpolationPoint( 0.4810379241516966 , 0.9966077966083999 ) );
		interps.add( new InterpolationPoint( 0.48303393213572854 , 0.9828872025682699 ) );
		interps.add( new InterpolationPoint( 0.48502994011976047 , 0.9967805105435757 ) );
		interps.add( new InterpolationPoint( 0.4870259481037924 , 0.9978174229006037 ) );
		interps.add( new InterpolationPoint( 0.48902195608782434 , 0.9970108423516983 ) );
		interps.add( new InterpolationPoint( 0.49101796407185627 , 1.0016286719487886 ) );
		interps.add( new InterpolationPoint( 0.4930139720558882 , 1.0031920119120281 ) );
		interps.add( new InterpolationPoint( 0.49500998003992014 , 1.0033658669036347 ) );
		interps.add( new InterpolationPoint( 0.49700598802395207 , 1.0026706276613164 ) );
		interps.add( new InterpolationPoint( 0.499001996007984 , 0.9962624585122168 ) );
		interps.add( new InterpolationPoint( 0.500998003992016 , 0.9967805105435755 ) );
		interps.add( new InterpolationPoint( 0.5029940119760479 , 1.0027864674054525 ) );
		interps.add( new InterpolationPoint( 0.5049900199600799 , 1.0017443913145279 ) );
		interps.add( new InterpolationPoint( 0.5069860279441117 , 0.9992593686369211 ) );
		interps.add( new InterpolationPoint( 0.5089820359281437 , 1.00498996709749 ) );
		interps.add( new InterpolationPoint( 0.5109780439121756 , 0.997356439862906 ) );
		interps.add( new InterpolationPoint( 0.5129740518962076 , 0.9927583066266771 ) );
		interps.add( new InterpolationPoint( 0.5149700598802395 , 1.0080713577085445 ) );
		interps.add( new InterpolationPoint( 0.5169660678642715 , 0.9940206758527246 ) );
		interps.add( new InterpolationPoint( 0.5189620758483033 , 0.9948823023561677 ) );
		interps.add( new InterpolationPoint( 0.5209580838323353 , 0.9955721412713646 ) );
		interps.add( new InterpolationPoint( 0.5229540918163673 , 1.0016286719487886 ) );
		interps.add( new InterpolationPoint( 0.5249500998003992 , 0.9983362835065034 ) );
		interps.add( new InterpolationPoint( 0.5269461077844312 , 1.0075474375384657 ) );
		interps.add( new InterpolationPoint( 0.5289421157684631 , 1.0017443913145276 ) );
		interps.add( new InterpolationPoint( 0.530938123752495 , 0.9943652368715933 ) );
		interps.add( new InterpolationPoint( 0.5329341317365269 , 0.995629649434147 ) );
		interps.add( new InterpolationPoint( 0.5349301397205589 , 0.9977021571698418 ) );
		interps.add( new InterpolationPoint( 0.5369261477045908 , 1.0026706276613164 ) );
		interps.add( new InterpolationPoint( 0.5389221556886228 , 1.0062097671951205 ) );
		interps.add( new InterpolationPoint( 0.5409181636726547 , 0.9972412273839172 ) );
		interps.add( new InterpolationPoint( 0.5429141716566867 , 1.0066166961444158 ) );
		interps.add( new InterpolationPoint( 0.5449101796407185 , 0.9997212312176301 ) );
		interps.add( new InterpolationPoint( 0.5469061876247505 , 1.0081295878752312 ) );
		interps.add( new InterpolationPoint( 0.5489021956087824 , 0.9989708128855036 ) );
		interps.add( new InterpolationPoint( 0.5508982035928144 , 1.0083625421799565 ) );
		interps.add( new InterpolationPoint( 0.5528942115768463 , 1.001917995430864 ) );
		interps.add( new InterpolationPoint( 0.5548902195608783 , 1.0083625421799565 ) );
		interps.add( new InterpolationPoint( 0.5568862275449101 , 1.0026706276613164 ) );
		interps.add( new InterpolationPoint( 0.5588822355289421 , 1.0068493008565433 ) );
		interps.add( new InterpolationPoint( 0.5608782435129741 , 0.9986246559721839 ) );
		interps.add( new InterpolationPoint( 0.562874251497006 , 0.9968956697953936 ) );
		interps.add( new InterpolationPoint( 0.564870259481038 , 1.0001833072734894 ) );
		interps.add( new InterpolationPoint( 0.5668662674650699 , 0.9971260282140272 ) );
		interps.add( new InterpolationPoint( 0.5688622754491018 , 0.9941929414351902 ) );
		interps.add( new InterpolationPoint( 0.5708582834331337 , 0.9951696771289091 ) );
		interps.add( new InterpolationPoint( 0.5728542914171657 , 0.9970684336195137 ) );
		interps.add( new InterpolationPoint( 0.5748502994011976 , 1.0111037908114167 ) );
		interps.add( new InterpolationPoint( 0.5768463073852296 , 1.001223759428838 ) );
		interps.add( new InterpolationPoint( 0.5788423153692615 , 1.0038876126756493 ) );
		interps.add( new InterpolationPoint( 0.5808383233532934 , 0.9946524622884003 ) );
		interps.add( new InterpolationPoint( 0.5828343313373253 , 0.9975292835393131 ) );
		interps.add( new InterpolationPoint( 0.5848303393213573 , 0.9855022696211172 ) );
		interps.add( new InterpolationPoint( 0.5868263473053892 , 1.0012237594288382 ) );
		interps.add( new InterpolationPoint( 0.5888223552894212 , 1.0012237594288382 ) );
		interps.add( new InterpolationPoint( 0.590818363273453 , 1.004699756523314 ) );
		interps.add( new InterpolationPoint( 0.592814371257485 , 1.0103448284160859 ) );
		interps.add( new InterpolationPoint( 0.5948103792415169 , 1.0053964027381452 ) );
		interps.add( new InterpolationPoint( 0.5968063872255489 , 0.9967805105435757 ) );
		interps.add( new InterpolationPoint( 0.5988023952095808 , 0.9986246559721841 ) );
		interps.add( new InterpolationPoint( 0.6007984031936128 , 0.9961473724076096 ) );
		interps.add( new InterpolationPoint( 0.6027944111776448 , 0.9956871609188266 ) );
		interps.add( new InterpolationPoint( 0.6047904191616766 , 0.9857299953012141 ) );
		interps.add( new InterpolationPoint( 0.6067864271457086 , 1.0026706276613164 ) );
		interps.add( new InterpolationPoint( 0.6087824351297405 , 1.0038296275279817 ) );
		interps.add( new InterpolationPoint( 0.6107784431137725 , 0.9856161258841902 ) );
		interps.add( new InterpolationPoint( 0.6127744510978044 , 1.0014551179640474 ) );
		interps.add( new InterpolationPoint( 0.6147704590818364 , 1.001281594051315 ) );
		interps.add( new InterpolationPoint( 0.6167664670658682 , 1.0027864674054525 ) );
		interps.add( new InterpolationPoint( 0.6187624750499002 , 0.998278619006922 ) );
		interps.add( new InterpolationPoint( 0.6207584830339321 , 0.9967229359063691 ) );
		interps.add( new InterpolationPoint( 0.6227544910179641 , 1.0013394320145463 ) );
		interps.add( new InterpolationPoint( 0.624750499001996 , 0.9990862251855865 ) );
		interps.add( new InterpolationPoint( 0.626746506986028 , 1.0099363923539506 ) );
		interps.add( new InterpolationPoint( 0.6287425149700598 , 1.0038296275279817 ) );
		interps.add( new InterpolationPoint( 0.6307385229540918 , 0.9968956697953936 ) );
		interps.add( new InterpolationPoint( 0.6327345309381237 , 1.002960252115462 ) );
		interps.add( new InterpolationPoint( 0.6347305389221557 , 0.9989131117351263 ) );
		interps.add( new InterpolationPoint( 0.6367265469061876 , 0.9999522425550001 ) );
		interps.add( new InterpolationPoint( 0.6387225548902196 , 1.0045836957601184 ) );
		interps.add( new InterpolationPoint( 0.6407185628742516 , 0.9967805105435757 ) );
		interps.add( new InterpolationPoint( 0.6427145708582834 , 1.0029023205326937 ) );
		interps.add( new InterpolationPoint( 0.6447105788423154 , 0.9944801170839376 ) );
		interps.add( new InterpolationPoint( 0.6467065868263473 , 1.002728545860593 ) );
		interps.add( new InterpolationPoint( 0.6487025948103793 , 1.0003566408511355 ) );
		interps.add( new InterpolationPoint( 0.6506986027944112 , 0.9940780943967619 ) );
		interps.add( new InterpolationPoint( 0.6526946107784432 , 1.0013972733187264 ) );
		interps.add( new InterpolationPoint( 0.654690618762475 , 1.009003444116776 ) );
		interps.add( new InterpolationPoint( 0.656686626746507 , 1.0028443922960883 ) );
		interps.add( new InterpolationPoint( 0.6586826347305389 , 0.9968956697953936 ) );
		interps.add( new InterpolationPoint( 0.6606786427145709 , 1.0082460582996098 ) );
		interps.add( new InterpolationPoint( 0.6626746506986028 , 0.9997212312176301 ) );
		interps.add( new InterpolationPoint( 0.6646706586826348 , 1.0001833072734894 ) );
		interps.add( new InterpolationPoint( 0.6666666666666666 , 0.9982209578380762 ) );
		interps.add( new InterpolationPoint( 0.6686626746506986 , 1.005106074797702 ) );
		interps.add( new InterpolationPoint( 0.6706586826347305 , 1.002728545860593 ) );
		interps.add( new InterpolationPoint( 0.6726546906187625 , 1.0120971404132697 ) );
		interps.add( new InterpolationPoint( 0.6746506986027944 , 0.9990862251855865 ) );
		interps.add( new InterpolationPoint( 0.6766467065868264 , 1.0040035930195284 ) );
		interps.add( new InterpolationPoint( 0.6786427145708582 , 0.9991439363356774 ) );
		interps.add( new InterpolationPoint( 0.6806387225548902 , 1.0103448284160859 ) );
		interps.add( new InterpolationPoint( 0.6826347305389222 , 1.0005300044678043 ) );
		interps.add( new InterpolationPoint( 0.6846307385229541 , 1.0026127128074296 ) );
		interps.add( new InterpolationPoint( 0.6866267465069861 , 1.0000100037296609 ) );
		interps.add( new InterpolationPoint( 0.688622754491018 , 1.0037716457295707 ) );
		interps.add( new InterpolationPoint( 0.6906187624750499 , 1.001050275604011 ) );
		interps.add( new InterpolationPoint( 0.6926147704590818 , 0.9977597883707204 ) );
		interps.add( new InterpolationPoint( 0.6946107784431138 , 0.9998944847166595 ) );
		interps.add( new InterpolationPoint( 0.6966067864271457 , 0.9911539732392498 ) );
		interps.add( new InterpolationPoint( 0.6986027944111777 , 0.9975869047543307 ) );
		interps.add( new InterpolationPoint( 0.7005988023952096 , 1.0035397520246259 ) );
		interps.add( new InterpolationPoint( 0.7025948103792415 , 0.9945375621671073 ) );
		interps.add( new InterpolationPoint( 0.7045908183632734 , 0.9909249944989126 ) );
		interps.add( new InterpolationPoint( 0.7065868263473054 , 0.9907532951594358 ) );
		interps.add( new InterpolationPoint( 0.7085828343313373 , 1.0169610554850703 ) );
		interps.add( new InterpolationPoint( 0.7105788423153693 , 0.9974716656525313 ) );
		interps.add( new InterpolationPoint( 0.7125748502994012 , 1.0029023205326937 ) );
		interps.add( new InterpolationPoint( 0.7145708582834331 , 1.001512965950703 ) );
		interps.add( new InterpolationPoint( 0.716566866267465 , 0.9910394772559158 ) );
		interps.add( new InterpolationPoint( 0.718562874251497 , 0.9855591961085047 ) );
		interps.add( new InterpolationPoint( 0.720558882235529 , 1.0017443913145276 ) );
		interps.add( new InterpolationPoint( 0.7225548902195609 , 1.003597720427944 ) );
		interps.add( new InterpolationPoint( 0.7245508982035929 , 1.0094698104569584 ) );
		interps.add( new InterpolationPoint( 0.7265469061876247 , 0.9991439363356774 ) );
		interps.add( new InterpolationPoint( 0.7285429141716567 , 1.0037136672802232 ) );
		interps.add( new InterpolationPoint( 0.7305389221556886 , 1.0038876126756493 ) );
		interps.add( new InterpolationPoint( 0.7325349301397206 , 1.007838470673946 ) );
		interps.add( new InterpolationPoint( 0.7345309381237525 , 1.0095864357188644 ) );
		interps.add( new InterpolationPoint( 0.7365269461077845 , 0.9965502319472532 ) );
		interps.add( new InterpolationPoint( 0.7385229540918163 , 0.9993170897884589 ) );
		interps.add( new InterpolationPoint( 0.7405189620758483 , 1.0073146715411465 ) );
		interps.add( new InterpolationPoint( 0.7425149700598802 , 0.9851038762657905 ) );
		interps.add( new InterpolationPoint( 0.7445109780439122 , 0.997702157169842 ) );
		interps.add( new InterpolationPoint( 0.7465069860279441 , 1.0084790395178262 ) );
		interps.add( new InterpolationPoint( 0.7485029940119761 , 1.0060935319987274 ) );
		interps.add( new InterpolationPoint( 0.7504990019960079 , 1.011220604849039 ) );
		interps.add( new InterpolationPoint( 0.7524950099800399 , 0.9639394506338518 ) );
		interps.add( new InterpolationPoint( 0.7544910179640718 , 0.9986823404602216 ) );
		interps.add( new InterpolationPoint( 0.7564870259481038 , 0.9912112261912681 ) );
		interps.add( new InterpolationPoint( 0.7584830339321357 , 1.0126234264085352 ) );
		interps.add( new InterpolationPoint( 0.7604790419161677 , 0.9898380676785848 ) );
		interps.add( new InterpolationPoint( 0.7624750499001997 , 0.9740702750990848 ) );
		interps.add( new InterpolationPoint( 0.7644710578842315 , 1.0085955503147732 ) );
		interps.add( new InterpolationPoint( 0.7664670658682635 , 0.9955146364302885 ) );
		interps.add( new InterpolationPoint( 0.7684630738522954 , 1.008945163475631 ) );
		interps.add( new InterpolationPoint( 0.7704590818363274 , 0.9989708128855036 ) );
		interps.add( new InterpolationPoint( 0.7724550898203593 , 0.9968956697953936 ) );
		interps.add( new InterpolationPoint( 0.7744510978043913 , 0.990581625570609 ) );
		interps.add( new InterpolationPoint( 0.7764471057884231 , 1.001281594051315 ) );
		interps.add( new InterpolationPoint( 0.7784431137724551 , 1.008945163475631 ) );
		interps.add( new InterpolationPoint( 0.780439121756487 , 0.9912112261912681 ) );
		interps.add( new InterpolationPoint( 0.782435129740519 , 0.991096723594196 ) );
		interps.add( new InterpolationPoint( 0.7844311377245509 , 1.0095281214037768 ) );
		interps.add( new InterpolationPoint( 0.7864271457085829 , 1.0092948978241763 ) );
		interps.add( new InterpolationPoint( 0.7884231536926147 , 1.006674842284129 ) );
		interps.add( new InterpolationPoint( 0.7904191616766467 , 1.0002410817953566 ) );
		interps.add( new InterpolationPoint( 0.7924151696606786 , 1.0010502756040112 ) );
		interps.add( new InterpolationPoint( 0.7944111776447106 , 1.0084790395178262 ) );
		interps.add( new InterpolationPoint( 0.7964071856287425 , 1.003018187044586 ) );
		interps.add( new InterpolationPoint( 0.7984031936127745 , 1.0102281155462807 ) );
		interps.add( new InterpolationPoint( 0.8003992015968064 , 1.0001833072734894 ) );
		interps.add( new InterpolationPoint( 0.8023952095808383 , 0.9854453464218377 ) );
		interps.add( new InterpolationPoint( 0.8043912175648703 , 0.9996634867226424 ) );
		interps.add( new InterpolationPoint( 0.8063872255489022 , 0.998855413917602 ) );
		interps.add( new InterpolationPoint( 0.8083832335329342 , 0.9987977194327383 ) );
		interps.add( new InterpolationPoint( 0.810379241516966 , 0.9997212312176301 ) );
		interps.add( new InterpolationPoint( 0.812375249500998 , 1.0061516479184218 ) );
		interps.add( new InterpolationPoint( 0.8143712574850299 , 1.0002410817953569 ) );
		interps.add( new InterpolationPoint( 0.8163672654690619 , 0.9972988319596783 ) );
		interps.add( new InterpolationPoint( 0.8183632734530938 , 1.0006455969031671 ) );
		interps.add( new InterpolationPoint( 0.8203592814371258 , 1.0136182689623774 ) );
		interps.add( new InterpolationPoint( 0.8223552894211577 , 0.9959172400803602 ) );
		interps.add( new InterpolationPoint( 0.8243512974051896 , 1.0085955503147732 ) );
		interps.add( new InterpolationPoint( 0.8263473053892215 , 0.9910394772559158 ) );
		interps.add( new InterpolationPoint( 0.8283433133732535 , 1.0023810868419156 ) );
		interps.add( new InterpolationPoint( 0.8303393213572854 , 0.9939632606252169 ) );
		interps.add( new InterpolationPoint( 0.8323353293413174 , 1.0033658669036347 ) );
		interps.add( new InterpolationPoint( 0.8343313373253493 , 1.001686529960605 ) );
		interps.add( new InterpolationPoint( 0.8363273453093812 , 1.0044096297531362 ) );
		interps.add( new InterpolationPoint( 0.8383233532934131 , 0.9935040581772273 ) );
		interps.add( new InterpolationPoint( 0.8403193612774451 , 0.9961473724076096 ) );
		interps.add( new InterpolationPoint( 0.8423153692614771 , 0.9974140510937932 ) );
		interps.add( new InterpolationPoint( 0.844311377245509 , 1.0020916296331082 ) );
		interps.add( new InterpolationPoint( 0.846307385229541 , 1.0029023205326937 ) );
		interps.add( new InterpolationPoint( 0.8483033932135728 , 0.9917266516043676 ) );
		interps.add( new InterpolationPoint( 0.8502994011976048 , 0.991955815588225 ) );
		interps.add( new InterpolationPoint( 0.8522954091816367 , 1.009353198667353 ) );
		interps.add( new InterpolationPoint( 0.8542914171656687 , 0.9951696771289091 ) );
		interps.add( new InterpolationPoint( 0.8562874251497006 , 1.0083042985576898 ) );
		interps.add( new InterpolationPoint( 0.8582834331337326 , 0.9943078017420355 ) );
		interps.add( new InterpolationPoint( 0.8602794411177644 , 1.006267889829017 ) );
		interps.add( new InterpolationPoint( 0.8622754491017964 , 0.9981632999997743 ) );
		interps.add( new InterpolationPoint( 0.8642714570858283 , 1.0065585533632582 ) );
		interps.add( new InterpolationPoint( 0.8662674650698603 , 0.9999522425549999 ) );
		interps.add( new InterpolationPoint( 0.8682634730538922 , 1.0052221959119767 ) );
		interps.add( new InterpolationPoint( 0.8702594810379242 , 0.9853315098868424 ) );
		interps.add( new InterpolationPoint( 0.872255489021956 , 0.9832846994617176 ) );
		interps.add( new InterpolationPoint( 0.874251497005988 , 1.0059192043797296 ) );
		interps.add( new InterpolationPoint( 0.8762475049900199 , 1.0016286719487888 ) );
		interps.add( new InterpolationPoint( 0.8782435129740519 , 0.9991439363356777 ) );
		interps.add( new InterpolationPoint( 0.8802395209580839 , 0.9963200065504381 ) );
		interps.add( new InterpolationPoint( 0.8822355289421158 , 0.997414051093793 ) );
		interps.add( new InterpolationPoint( 0.8842315369261478 , 0.9998944847166595 ) );
		interps.add( new InterpolationPoint( 0.8862275449101796 , 0.999374814274197 ) );
		interps.add( new InterpolationPoint( 0.8882235528942116 , 0.9993170897884589 ) );
		interps.add( new InterpolationPoint( 0.8902195608782435 , 1.0052802614998715 ) );
		interps.add( new InterpolationPoint( 0.8922155688622755 , 1.0038296275279817 ) );
		interps.add( new InterpolationPoint( 0.8942115768463074 , 0.9619370775269844 ) );
		interps.add( new InterpolationPoint( 0.8962075848303394 , 1.0058611018861128 ) );
		interps.add( new InterpolationPoint( 0.8982035928143712 , 0.9856161258841902 ) );
		interps.add( new InterpolationPoint( 0.9001996007984032 , 0.9980479943140311 ) );
		interps.add( new InterpolationPoint( 0.9021956087824351 , 0.9911539732392498 ) );
		interps.add( new InterpolationPoint( 0.9041916167664671 , 1.0026706276613164 ) );
		interps.add( new InterpolationPoint( 0.906187624750499 , 1.00906172812444 ) );
		interps.add( new InterpolationPoint( 0.908183632734531 , 0.9983362835065032 ) );
		interps.add( new InterpolationPoint( 0.9101796407185628 , 1.0092948978241763 ) );
		interps.add( new InterpolationPoint( 0.9121756487025948 , 1.0167848448309136 ) );
		interps.add( new InterpolationPoint( 0.9141716566866267 , 0.9997789790481671 ) );
		interps.add( new InterpolationPoint( 0.9161676646706587 , 0.9940780943967616 ) );
		interps.add( new InterpolationPoint( 0.9181636726546906 , 1.008013130905267 ) );
		interps.add( new InterpolationPoint( 0.9201596806387226 , 1.0080713577085447 ) );
		interps.add( new InterpolationPoint( 0.9221556886227545 , 1.0081295878752314 ) );
		interps.add( new InterpolationPoint( 0.9241516966067864 , 1.0163150990079834 ) );
		interps.add( new InterpolationPoint( 0.9261477045908184 , 0.9990285173689261 ) );
		interps.add( new InterpolationPoint( 0.9281437125748503 , 1.0333634527555242 ) );
		interps.add( new InterpolationPoint( 0.9301397205588823 , 1.0029023205326937 ) );
		interps.add( new InterpolationPoint( 0.9321357285429142 , 0.9684041043124824 ) );
		interps.add( new InterpolationPoint( 0.9341317365269461 , 1.0079549074652039 ) );
		interps.add( new InterpolationPoint( 0.936127744510978 , 1.0109869902679012 ) );
		interps.add( new InterpolationPoint( 0.93812375249501 , 1.0250998427265285 ) );
		interps.add( new InterpolationPoint( 0.9401197604790419 , 0.9948823023561675 ) );
		interps.add( new InterpolationPoint( 0.9421157684630739 , 1.040130416920559 ) );
		interps.add( new InterpolationPoint( 0.9441117764471058 , 0.9856730589483634 ) );
		interps.add( new InterpolationPoint( 0.9461077844311377 , 1.003018187044586 ) );
		interps.add( new InterpolationPoint( 0.9481037924151696 , 1.004641724465733 ) );
		interps.add( new InterpolationPoint( 0.9500998003992016 , 0.9930450678768052 ) );
		interps.add( new InterpolationPoint( 0.9520958083832335 , 1.0110453888529929 ) );
		interps.add( new InterpolationPoint( 0.9540918163672655 , 0.994595010568531 ) );
		interps.add( new InterpolationPoint( 0.9560878243512974 , 0.9986823404602216 ) );
		interps.add( new InterpolationPoint( 0.9580838323353293 , 0.9972412273839172 ) );
		interps.add( new InterpolationPoint( 0.9600798403193613 , 0.998855413917602 ) );
		interps.add( new InterpolationPoint( 0.9620758483033932 , 0.9983362835065034 ) );
		interps.add( new InterpolationPoint( 0.9640718562874252 , 0.9925862896259412 ) );
		interps.add( new InterpolationPoint( 0.9660678642714571 , 0.997644529297776 ) );
		interps.add( new InterpolationPoint( 0.9680638722554891 , 1.0038876126756493 ) );
		interps.add( new InterpolationPoint( 0.9700598802395209 , 0.9928156522513023 ) );
		interps.add( new InterpolationPoint( 0.9720558882235529 , 0.9935040581772273 ) );
		interps.add( new InterpolationPoint( 0.9740518962075848 , 0.994595010568531 ) );
		interps.add( new InterpolationPoint( 0.9760479041916168 , 1.0023231887120791 ) );
		interps.add( new InterpolationPoint( 0.9780439121756487 , 0.9868694132731823 ) );
		interps.add( new InterpolationPoint( 0.9800399201596807 , 0.9834551044800408 ) );
		interps.add( new InterpolationPoint( 0.9820359281437125 , 1.0077802573223624 ) );
		interps.add( new InterpolationPoint( 0.9840319361277445 , 1.0041195867627564 ) );
		interps.add( new InterpolationPoint( 0.9860279441117764 , 1.0113374323823277 ) );
		interps.add( new InterpolationPoint( 0.9880239520958084 , 0.9985669748160367 ) );
		interps.add( new InterpolationPoint( 0.9900199600798403 , 0.9892093392650082 ) );
		interps.add( new InterpolationPoint( 0.9920159680638723 , 0.9892664798873193 ) );
		interps.add( new InterpolationPoint( 0.9940119760479041 , 0.9877818958665798 ) );
		interps.add( new InterpolationPoint( 0.9960079840319361 , 0.9892664798873193 ) );
		interps.add( new InterpolationPoint( 0.998003992015968 , 0.9857869349429328 ) );
		interps.add( new InterpolationPoint( 1.0 , 0.9925862896259408 ) );
		return( interps );
	}
	
	/**
	 * Creates the interpolation points of the default amplitude envelope of a note parameterized over the barycentric duration of the note.
	 * @param interps The list into which to populate the interpolation points of the envelope.
	 */
	public static void createInitialEnvelope( ArrayList<InterpolationPoint> interps )
	{
		interps.add( new InterpolationPoint( 0.0 , 0.9977003355288195 ) );
		interps.add( new InterpolationPoint( 0.0017709563164108617 , 1.0 ) );
		interps.add( new InterpolationPoint( 0.0035419126328217233 , 0.9772468016948959 ) );
		interps.add( new InterpolationPoint( 0.005312868949232585 , 0.9667860170960616 ) );
		interps.add( new InterpolationPoint( 0.007083825265643447 , 0.9725566439369887 ) );
		interps.add( new InterpolationPoint( 0.008854781582054308 , 0.9678625481090637 ) );
		interps.add( new InterpolationPoint( 0.01062573789846517 , 0.9572671237079833 ) );
		interps.add( new InterpolationPoint( 0.012396694214876033 , 0.9462447656142554 ) );
		interps.add( new InterpolationPoint( 0.014167650531286893 , 0.9343931252053006 ) );
		interps.add( new InterpolationPoint( 0.015938606847697757 , 0.9300441848021702 ) );
		interps.add( new InterpolationPoint( 0.017709563164108617 , 0.9381426695563887 ) );
		interps.add( new InterpolationPoint( 0.01948051948051948 , 0.9299958672387428 ) );
		interps.add( new InterpolationPoint( 0.02125147579693034 , 0.9010241439534581 ) );
		interps.add( new InterpolationPoint( 0.023022432113341207 , 0.8904296277159873 ) );
		interps.add( new InterpolationPoint( 0.024793388429752067 , 0.9135286411301243 ) );
		interps.add( new InterpolationPoint( 0.026564344746162927 , 0.9021357295209709 ) );
		interps.add( new InterpolationPoint( 0.028335301062573787 , 0.864615984377786 ) );
		interps.add( new InterpolationPoint( 0.03010625737898465 , 0.8569747532116466 ) );
		interps.add( new InterpolationPoint( 0.031877213695395513 , 0.8830781301860674 ) );
		interps.add( new InterpolationPoint( 0.033648170011806366 , 0.8643490038118079 ) );
		interps.add( new InterpolationPoint( 0.03541912632821723 , 0.8179436483098612 ) );
		interps.add( new InterpolationPoint( 0.0371900826446281 , 0.8121244290907869 ) );
		interps.add( new InterpolationPoint( 0.03896103896103896 , 0.8390325124052165 ) );
		interps.add( new InterpolationPoint( 0.04073199527744981 , 0.8233490817278944 ) );
		interps.add( new InterpolationPoint( 0.04250295159386068 , 0.7737734739541874 ) );
		interps.add( new InterpolationPoint( 0.04427390791027154 , 0.7633421411660198 ) );
		interps.add( new InterpolationPoint( 0.046044864226682414 , 0.7819705591996308 ) );
		interps.add( new InterpolationPoint( 0.04781582054309326 , 0.7856405820318438 ) );
		interps.add( new InterpolationPoint( 0.049586776859504134 , 0.7449293918003653 ) );
		interps.add( new InterpolationPoint( 0.051357733175914994 , 0.7147393101173439 ) );
		interps.add( new InterpolationPoint( 0.053128689492325853 , 0.7286499990103689 ) );
		interps.add( new InterpolationPoint( 0.05489964580873671 , 0.7503387817921506 ) );
		interps.add( new InterpolationPoint( 0.05667060212514757 , 0.7262046764651049 ) );
		interps.add( new InterpolationPoint( 0.05844155844155844 , 0.6844357535240431 ) );
		interps.add( new InterpolationPoint( 0.0602125147579693 , 0.687519371300952 ) );
		interps.add( new InterpolationPoint( 0.06198347107438017 , 0.7295851324563568 ) );
		interps.add( new InterpolationPoint( 0.06375442739079103 , 0.7177955234734507 ) );
		interps.add( new InterpolationPoint( 0.06552538370720187 , 0.6676892426831729 ) );
		interps.add( new InterpolationPoint( 0.06729634002361273 , 0.6705910637536604 ) );
		interps.add( new InterpolationPoint( 0.0690672963400236 , 0.7143026363623706 ) );
		interps.add( new InterpolationPoint( 0.07083825265643447 , 0.7068959618236255 ) );
		interps.add( new InterpolationPoint( 0.07260920897284533 , 0.657861686341905 ) );
		interps.add( new InterpolationPoint( 0.0743801652892562 , 0.656840175790528 ) );
		interps.add( new InterpolationPoint( 0.07615112160566706 , 0.7037785871610707 ) );
		interps.add( new InterpolationPoint( 0.07792207792207792 , 0.6999327846633966 ) );
		interps.add( new InterpolationPoint( 0.07969303423848877 , 0.6530352541025607 ) );
		interps.add( new InterpolationPoint( 0.08146399055489963 , 0.6508578726443043 ) );
		interps.add( new InterpolationPoint( 0.0832349468713105 , 0.6863273329532642 ) );
		interps.add( new InterpolationPoint( 0.08500590318772136 , 0.7040620086956475 ) );
		interps.add( new InterpolationPoint( 0.08677685950413222 , 0.6683583026914721 ) );
		interps.add( new InterpolationPoint( 0.08854781582054308 , 0.6487027639994074 ) );
		interps.add( new InterpolationPoint( 0.09031877213695395 , 0.6758071845691423 ) );
		interps.add( new InterpolationPoint( 0.09208972845336483 , 0.7096890626008076 ) );
		interps.add( new InterpolationPoint( 0.09386068476977567 , 0.6941879319215578 ) );
		interps.add( new InterpolationPoint( 0.09563164108618652 , 0.6655096448435301 ) );
		interps.add( new InterpolationPoint( 0.0974025974025974 , 0.6738709598547966 ) );
		interps.add( new InterpolationPoint( 0.09917355371900827 , 0.7195699178267521 ) );
		interps.add( new InterpolationPoint( 0.10094451003541911 , 0.7213277560084878 ) );
		interps.add( new InterpolationPoint( 0.10271546635182999 , 0.6791986881514335 ) );
		interps.add( new InterpolationPoint( 0.10448642266824085 , 0.6816505729731187 ) );
		interps.add( new InterpolationPoint( 0.10625737898465171 , 0.7233530583261173 ) );
		interps.add( new InterpolationPoint( 0.10802833530106255 , 0.7303559629921792 ) );
		interps.add( new InterpolationPoint( 0.10979929161747343 , 0.6873101482988537 ) );
		interps.add( new InterpolationPoint( 0.11157024793388429 , 0.6819736612264153 ) );
		interps.add( new InterpolationPoint( 0.11334120425029515 , 0.7242142277953862 ) );
		interps.add( new InterpolationPoint( 0.11511216056670599 , 0.7254257908019661 ) );
		interps.add( new InterpolationPoint( 0.11688311688311688 , 0.6893142503186603 ) );
		interps.add( new InterpolationPoint( 0.11865407319952773 , 0.6822989156454681 ) );
		interps.add( new InterpolationPoint( 0.1204250295159386 , 0.7074605972177924 ) );
		interps.add( new InterpolationPoint( 0.12219598583234945 , 0.7186342777180005 ) );
		interps.add( new InterpolationPoint( 0.12396694214876033 , 0.6936217660326586 ) );
		interps.add( new InterpolationPoint( 0.1257378984651712 , 0.6762588365033025 ) );
		interps.add( new InterpolationPoint( 0.12750885478158205 , 0.6854178354315031 ) );
		interps.add( new InterpolationPoint( 0.1292798110979929 , 0.7081739401932318 ) );
		interps.add( new InterpolationPoint( 0.13105076741440375 , 0.6989547159943039 ) );
		interps.add( new InterpolationPoint( 0.13282172373081463 , 0.6753743659908167 ) );
		interps.add( new InterpolationPoint( 0.13459268004722547 , 0.664785241585352 ) );
		interps.add( new InterpolationPoint( 0.13636363636363635 , 0.691007579962305 ) );
		interps.add( new InterpolationPoint( 0.1381345926800472 , 0.6982121197155466 ) );
		interps.add( new InterpolationPoint( 0.13990554899645807 , 0.6630891675214674 ) );
		interps.add( new InterpolationPoint( 0.14167650531286893 , 0.6491585065785521 ) );
		interps.add( new InterpolationPoint( 0.1434474616292798 , 0.6700513103449055 ) );
		interps.add( new InterpolationPoint( 0.14521841794569065 , 0.6789495385762409 ) );
		interps.add( new InterpolationPoint( 0.1469893742621015 , 0.645541842092899 ) );
		interps.add( new InterpolationPoint( 0.1487603305785124 , 0.6306558464834309 ) );
		interps.add( new InterpolationPoint( 0.15053128689492323 , 0.6516510041606731 ) );
		interps.add( new InterpolationPoint( 0.15230224321133412 , 0.657208568981659 ) );
		interps.add( new InterpolationPoint( 0.15407319952774495 , 0.6301610890649518 ) );
		interps.add( new InterpolationPoint( 0.15584415584415584 , 0.6239368615704394 ) );
		interps.add( new InterpolationPoint( 0.15761511216056667 , 0.637521106891533 ) );
		interps.add( new InterpolationPoint( 0.15938606847697753 , 0.6402210974899146 ) );
		interps.add( new InterpolationPoint( 0.1611570247933884 , 0.6242345524238029 ) );
		interps.add( new InterpolationPoint( 0.16292798110979925 , 0.623291663763507 ) );
		interps.add( new InterpolationPoint( 0.16469893742621014 , 0.6252939905620888 ) );
		interps.add( new InterpolationPoint( 0.166469893742621 , 0.6276968449247174 ) );
		interps.add( new InterpolationPoint( 0.1682408500590319 , 0.6289353674512145 ) );
		interps.add( new InterpolationPoint( 0.17001180637544272 , 0.6275539581669751 ) );
		interps.add( new InterpolationPoint( 0.17178276269185358 , 0.6158449458457396 ) );
		interps.add( new InterpolationPoint( 0.17355371900826444 , 0.6159099550281931 ) );
		interps.add( new InterpolationPoint( 0.17532467532467527 , 0.6287356058847478 ) );
		interps.add( new InterpolationPoint( 0.17709563164108616 , 0.6274936155275584 ) );
		interps.add( new InterpolationPoint( 0.17886658795749702 , 0.6163737510058952 ) );
		interps.add( new InterpolationPoint( 0.1806375442739079 , 0.6112601897025235 ) );
		interps.add( new InterpolationPoint( 0.18240850059031877 , 0.6206215778431736 ) );
		interps.add( new InterpolationPoint( 0.18417945690672965 , 0.6174069734142478 ) );
		interps.add( new InterpolationPoint( 0.1859504132231405 , 0.6160942945748448 ) );
		interps.add( new InterpolationPoint( 0.18772136953955135 , 0.6130226177254962 ) );
		interps.add( new InterpolationPoint( 0.1894923258559622 , 0.6085440755284379 ) );
		interps.add( new InterpolationPoint( 0.19126328217237304 , 0.6089284393882669 ) );
		interps.add( new InterpolationPoint( 0.19303423848878393 , 0.6177687403496536 ) );
		interps.add( new InterpolationPoint( 0.1948051948051948 , 0.6152430913751826 ) );
		interps.add( new InterpolationPoint( 0.19657615112160567 , 0.6005180280473317 ) );
		interps.add( new InterpolationPoint( 0.19834710743801653 , 0.6021684959896527 ) );
		interps.add( new InterpolationPoint( 0.20011806375442737 , 0.6232962892204817 ) );
		interps.add( new InterpolationPoint( 0.20188902007083823 , 0.6159799260588715 ) );
		interps.add( new InterpolationPoint( 0.2036599763872491 , 0.5920186763147374 ) );
		interps.add( new InterpolationPoint( 0.20543093270365997 , 0.5996389630412559 ) );
		interps.add( new InterpolationPoint( 0.2072018890200708 , 0.62122949858846 ) );
		interps.add( new InterpolationPoint( 0.2089728453364817 , 0.60798173658657 ) );
		interps.add( new InterpolationPoint( 0.21074380165289255 , 0.5789473848166788 ) );
		interps.add( new InterpolationPoint( 0.21251475796930341 , 0.5829483521896762 ) );
		interps.add( new InterpolationPoint( 0.21428571428571425 , 0.6058195356740167 ) );
		interps.add( new InterpolationPoint( 0.2160566706021251 , 0.5935112265503075 ) );
		interps.add( new InterpolationPoint( 0.217827626918536 , 0.5606480241956449 ) );
		interps.add( new InterpolationPoint( 0.21959858323494685 , 0.5563581294804477 ) );
		interps.add( new InterpolationPoint( 0.22136953955135774 , 0.5718940421935544 ) );
		interps.add( new InterpolationPoint( 0.22314049586776857 , 0.5737202977470205 ) );
		interps.add( new InterpolationPoint( 0.22491145218417943 , 0.5448367955013903 ) );
		interps.add( new InterpolationPoint( 0.2266824085005903 , 0.5262008346770815 ) );
		interps.add( new InterpolationPoint( 0.22845336481700113 , 0.5340556281793228 ) );
		interps.add( new InterpolationPoint( 0.23022432113341199 , 0.5490573101447684 ) );
		interps.add( new InterpolationPoint( 0.2319952774498229 , 0.5297368533980645 ) );
		interps.add( new InterpolationPoint( 0.23376623376623376 , 0.49993713287090524 ) );
		interps.add( new InterpolationPoint( 0.23553719008264462 , 0.49893413544229187 ) );
		interps.add( new InterpolationPoint( 0.23730814639905545 , 0.5265402056835828 ) );
		interps.add( new InterpolationPoint( 0.23907910271546634 , 0.5115991077999466 ) );
		interps.add( new InterpolationPoint( 0.2408500590318772 , 0.47400293086171297 ) );
		interps.add( new InterpolationPoint( 0.24262101534828806 , 0.4737198900694278 ) );
		interps.add( new InterpolationPoint( 0.2443919716646989 , 0.49938005376781924 ) );
		interps.add( new InterpolationPoint( 0.24616292798110975 , 0.49109023302371774 ) );
		interps.add( new InterpolationPoint( 0.24793388429752067 , 0.45063933121012606 ) );
		interps.add( new InterpolationPoint( 0.24970484061393153 , 0.4469300586602515 ) );
		interps.add( new InterpolationPoint( 0.2514757969303424 , 0.4701059806528644 ) );
		interps.add( new InterpolationPoint( 0.25324675324675316 , 0.4695877715393898 ) );
		interps.add( new InterpolationPoint( 0.2550177095631641 , 0.43506027288404886 ) );
		interps.add( new InterpolationPoint( 0.25678866587957494 , 0.42390949671553657 ) );
		interps.add( new InterpolationPoint( 0.2585596221959858 , 0.4401099772825064 ) );
		interps.add( new InterpolationPoint( 0.26033057851239666 , 0.4535580952979811 ) );
		interps.add( new InterpolationPoint( 0.2621015348288075 , 0.4352697878810731 ) );
		interps.add( new InterpolationPoint( 0.2638724911452184 , 0.41320321514078906 ) );
		interps.add( new InterpolationPoint( 0.26564344746162927 , 0.42145943168384237 ) );
		interps.add( new InterpolationPoint( 0.2674144037780401 , 0.44690129776928617 ) );
		interps.add( new InterpolationPoint( 0.26918536009445093 , 0.4425685660791717 ) );
		interps.add( new InterpolationPoint( 0.2709563164108618 , 0.41664449027751554 ) );
		interps.add( new InterpolationPoint( 0.2727272727272727 , 0.4147812877000487 ) );
		interps.add( new InterpolationPoint( 0.2744982290436836 , 0.4466157434972646 ) );
		interps.add( new InterpolationPoint( 0.2762691853600944 , 0.4521969736722513 ) );
		interps.add( new InterpolationPoint( 0.27804014167650526 , 0.4241096108075283 ) );
		interps.add( new InterpolationPoint( 0.27981109799291615 , 0.42020964540576344 ) );
		interps.add( new InterpolationPoint( 0.28158205430932703 , 0.45011956304901124 ) );
		interps.add( new InterpolationPoint( 0.28335301062573787 , 0.45905312546962695 ) );
		interps.add( new InterpolationPoint( 0.2851239669421487 , 0.43457025870992594 ) );
		interps.add( new InterpolationPoint( 0.2868949232585596 , 0.4281434267271372 ) );
		interps.add( new InterpolationPoint( 0.2886658795749705 , 0.4515869105988685 ) );
		interps.add( new InterpolationPoint( 0.2904368358913813 , 0.46741864531446214 ) );
		interps.add( new InterpolationPoint( 0.29220779220779214 , 0.4510872863256002 ) );
		interps.add( new InterpolationPoint( 0.293978748524203 , 0.4414535838040393 ) );
		interps.add( new InterpolationPoint( 0.2957497048406139 , 0.4526478831269622 ) );
		interps.add( new InterpolationPoint( 0.2975206611570248 , 0.47619189983988053 ) );
		interps.add( new InterpolationPoint( 0.29929161747343563 , 0.475452096229568 ) );
		interps.add( new InterpolationPoint( 0.30106257378984647 , 0.4576781717801818 ) );
		interps.add( new InterpolationPoint( 0.30283353010625735 , 0.4557236604526551 ) );
		interps.add( new InterpolationPoint( 0.30460448642266824 , 0.4827915978980845 ) );
		interps.add( new InterpolationPoint( 0.3063754427390791 , 0.4942039225904094 ) );
		interps.add( new InterpolationPoint( 0.3081463990554899 , 0.4716328937770877 ) );
		interps.add( new InterpolationPoint( 0.3099173553719008 , 0.45902428564021186 ) );
		interps.add( new InterpolationPoint( 0.3116883116883117 , 0.483620427268157 ) );
		interps.add( new InterpolationPoint( 0.3134592680047225 , 0.4990168139305456 ) );
		interps.add( new InterpolationPoint( 0.31523022432113335 , 0.4754978625033307 ) );
		interps.add( new InterpolationPoint( 0.31700118063754423 , 0.46169235061447583 ) );
		interps.add( new InterpolationPoint( 0.31877213695395507 , 0.47988821599289233 ) );
		interps.add( new InterpolationPoint( 0.320543093270366 , 0.49042081743624977 ) );
		interps.add( new InterpolationPoint( 0.3223140495867768 , 0.4679257325075061 ) );
		interps.add( new InterpolationPoint( 0.3240850059031877 , 0.45624211497998074 ) );
		interps.add( new InterpolationPoint( 0.3258559622195985 , 0.4675250037695215 ) );
		interps.add( new InterpolationPoint( 0.32762691853600934 , 0.4719915473377266 ) );
		interps.add( new InterpolationPoint( 0.3293978748524203 , 0.4580147371835105 ) );
		interps.add( new InterpolationPoint( 0.3311688311688311 , 0.4490237555329117 ) );
		interps.add( new InterpolationPoint( 0.332939787485242 , 0.44803334728428174 ) );
		interps.add( new InterpolationPoint( 0.33471074380165283 , 0.4510759005255933 ) );
		interps.add( new InterpolationPoint( 0.3364817001180638 , 0.44818118111560906 ) );
		interps.add( new InterpolationPoint( 0.3382526564344746 , 0.4407275026271561 ) );
		interps.add( new InterpolationPoint( 0.34002361275088544 , 0.4276335110690257 ) );
		interps.add( new InterpolationPoint( 0.3417945690672963 , 0.4275374092439116 ) );
		interps.add( new InterpolationPoint( 0.34356552538370716 , 0.4332592791852745 ) );
		interps.add( new InterpolationPoint( 0.345336481700118 , 0.42444702543135227 ) );
		interps.add( new InterpolationPoint( 0.3471074380165289 , 0.4078283993772075 ) );
		interps.add( new InterpolationPoint( 0.3488783943329397 , 0.4037154868832542 ) );
		interps.add( new InterpolationPoint( 0.35064935064935054 , 0.40819401473051514 ) );
		interps.add( new InterpolationPoint( 0.3524203069657615 , 0.39991864962517093 ) );
		interps.add( new InterpolationPoint( 0.3541912632821723 , 0.39126285895021756 ) );
		interps.add( new InterpolationPoint( 0.3559622195985832 , 0.38437210794440363 ) );
		interps.add( new InterpolationPoint( 0.35773317591499404 , 0.38116939982015136 ) );
		interps.add( new InterpolationPoint( 0.35950413223140487 , 0.3753673818722388 ) );
		interps.add( new InterpolationPoint( 0.3612750885478158 , 0.37835309386166943 ) );
		interps.add( new InterpolationPoint( 0.36304604486422665 , 0.37322581860336196 ) );
		interps.add( new InterpolationPoint( 0.36481700118063753 , 0.3594352919114636 ) );
		interps.add( new InterpolationPoint( 0.36658795749704837 , 0.35964467974888586 ) );
		interps.add( new InterpolationPoint( 0.3683589138134593 , 0.3709188729558524 ) );
		interps.add( new InterpolationPoint( 0.37012987012987014 , 0.36278690018721155 ) );
		interps.add( new InterpolationPoint( 0.371900826446281 , 0.3454932787692494 ) );
		interps.add( new InterpolationPoint( 0.37367178276269186 , 0.34990380856209174 ) );
		interps.add( new InterpolationPoint( 0.3754427390791027 , 0.36800027741801894 ) );
		interps.add( new InterpolationPoint( 0.3772136953955135 , 0.3592496469014017 ) );
		interps.add( new InterpolationPoint( 0.3789846517119244 , 0.3363636286307712 ) );
		interps.add( new InterpolationPoint( 0.38075560802833525 , 0.34393966855629465 ) );
		interps.add( new InterpolationPoint( 0.3825265643447461 , 0.36317141297088806 ) );
		interps.add( new InterpolationPoint( 0.38429752066115697 , 0.3566459966193472 ) );
		interps.add( new InterpolationPoint( 0.38606847697756785 , 0.3352594971223599 ) );
		interps.add( new InterpolationPoint( 0.38783943329397874 , 0.3370290167153591 ) );
		interps.add( new InterpolationPoint( 0.3896103896103896 , 0.35207127446581554 ) );
		interps.add( new InterpolationPoint( 0.3913813459268004 , 0.35767256001365244 ) );
		interps.add( new InterpolationPoint( 0.39315230224321135 , 0.34009326362115117 ) );
		interps.add( new InterpolationPoint( 0.3949232585596222 , 0.33218400069614523 ) );
		interps.add( new InterpolationPoint( 0.39669421487603307 , 0.34075632226370295 ) );
		interps.add( new InterpolationPoint( 0.3984651711924439 , 0.3575361281752749 ) );
		interps.add( new InterpolationPoint( 0.40023612750885473 , 0.35009622236046906 ) );
		interps.add( new InterpolationPoint( 0.4020070838252656 , 0.3297150967621528 ) );
		interps.add( new InterpolationPoint( 0.40377804014167645 , 0.3349642070270386 ) );
		interps.add( new InterpolationPoint( 0.4055489964580873 , 0.35772336683038536 ) );
		interps.add( new InterpolationPoint( 0.4073199527744982 , 0.35295515470147737 ) );
		interps.add( new InterpolationPoint( 0.40909090909090906 , 0.32743954087763977 ) );
		interps.add( new InterpolationPoint( 0.41086186540731995 , 0.32947923573833404 ) );
		interps.add( new InterpolationPoint( 0.4126328217237308 , 0.35221333324785514 ) );
		interps.add( new InterpolationPoint( 0.4144037780401416 , 0.3489943660316991 ) );
		interps.add( new InterpolationPoint( 0.4161747343565525 , 0.32040720916121607 ) );
		interps.add( new InterpolationPoint( 0.4179456906729634 , 0.31962896657271817 ) );
		interps.add( new InterpolationPoint( 0.4197166469893743 , 0.33852259585916894 ) );
		interps.add( new InterpolationPoint( 0.4214876033057851 , 0.337438722611462 ) );
		interps.add( new InterpolationPoint( 0.42325855962219594 , 0.314092260104245 ) );
		interps.add( new InterpolationPoint( 0.42502951593860683 , 0.3055449097144667 ) );
		interps.add( new InterpolationPoint( 0.42680047225501766 , 0.3173654322076962 ) );
		interps.add( new InterpolationPoint( 0.4285714285714285 , 0.32435221271566067 ) );
		interps.add( new InterpolationPoint( 0.4303423848878394 , 0.3085140070894798 ) );
		interps.add( new InterpolationPoint( 0.4321133412042502 , 0.29497794034061003 ) );
		interps.add( new InterpolationPoint( 0.43388429752066116 , 0.2956050801063674 ) );
		interps.add( new InterpolationPoint( 0.435655253837072 , 0.3085337523270703 ) );
		interps.add( new InterpolationPoint( 0.4374262101534828 , 0.30466720184467927 ) );
		interps.add( new InterpolationPoint( 0.4391971664698937 , 0.2843114238816516 ) );
		interps.add( new InterpolationPoint( 0.44096812278630454 , 0.27833518328753987 ) );
		interps.add( new InterpolationPoint( 0.4427390791027155 , 0.2936526433192471 ) );
		interps.add( new InterpolationPoint( 0.4445100354191263 , 0.29608325861442164 ) );
		interps.add( new InterpolationPoint( 0.44628099173553715 , 0.27475403587252906 ) );
		interps.add( new InterpolationPoint( 0.44805194805194803 , 0.2659505047574082 ) );
		interps.add( new InterpolationPoint( 0.44982290436835887 , 0.2801752662626341 ) );
		interps.add( new InterpolationPoint( 0.4515938606847697 , 0.2850032615235509 ) );
		interps.add( new InterpolationPoint( 0.4533648170011806 , 0.26601200367838645 ) );
		interps.add( new InterpolationPoint( 0.4551357733175915 , 0.257243128712526 ) );
		interps.add( new InterpolationPoint( 0.45690672963400225 , 0.26771211134056544 ) );
		interps.add( new InterpolationPoint( 0.4586776859504132 , 0.27442704352037656 ) );
		interps.add( new InterpolationPoint( 0.46044864226682397 , 0.26505279605559945 ) );
		interps.add( new InterpolationPoint( 0.4622195985832349 , 0.2548608163107992 ) );
		interps.add( new InterpolationPoint( 0.4639905548996458 , 0.2579721977068623 ) );
		interps.add( new InterpolationPoint( 0.4657615112160566 , 0.270043549583276 ) );
		interps.add( new InterpolationPoint( 0.4675324675324675 , 0.2704594460644408 ) );
		interps.add( new InterpolationPoint( 0.4693034238488783 , 0.26065693247854316 ) );
		interps.add( new InterpolationPoint( 0.47107438016528924 , 0.25489163628142775 ) );
		interps.add( new InterpolationPoint( 0.4728453364817001 , 0.26989630891196903 ) );
		interps.add( new InterpolationPoint( 0.4746162927981109 , 0.2802742714127826 ) );
		interps.add( new InterpolationPoint( 0.4763872491145218 , 0.2695470854906432 ) );
		interps.add( new InterpolationPoint( 0.4781582054309327 , 0.2620205342936943 ) );
		interps.add( new InterpolationPoint( 0.47992916174734346 , 0.274976656242295 ) );
		interps.add( new InterpolationPoint( 0.4817001180637544 , 0.28746049326375467 ) );
		interps.add( new InterpolationPoint( 0.4834710743801652 , 0.2774302365478734 ) );
		interps.add( new InterpolationPoint( 0.4852420306965761 , 0.2701775793561366 ) );
		interps.add( new InterpolationPoint( 0.487012987012987 , 0.2816760194835726 ) );
		interps.add( new InterpolationPoint( 0.4887839433293978 , 0.2896743708673061 ) );
		interps.add( new InterpolationPoint( 0.49055489964580873 , 0.2829037621868581 ) );
		interps.add( new InterpolationPoint( 0.4923258559622195 , 0.27989550652895184 ) );
		interps.add( new InterpolationPoint( 0.49409681227863045 , 0.28508746341527424 ) );
		interps.add( new InterpolationPoint( 0.49586776859504134 , 0.29150385323283895 ) );
		interps.add( new InterpolationPoint( 0.4976387249114521 , 0.28781840531047304 ) );
		interps.add( new InterpolationPoint( 0.49940968122786306 , 0.287793041293042 ) );
		interps.add( new InterpolationPoint( 0.5011806375442738 , 0.28703795013938654 ) );
		interps.add( new InterpolationPoint( 0.5029515938606848 , 0.2899674307403441 ) );
		interps.add( new InterpolationPoint( 0.5047225501770956 , 0.2945698019752167 ) );
		interps.add( new InterpolationPoint( 0.5064935064935063 , 0.2924577862274391 ) );
		interps.add( new InterpolationPoint( 0.5082644628099173 , 0.28409195747607274 ) );
		interps.add( new InterpolationPoint( 0.5100354191263282 , 0.2843426676884209 ) );
		interps.add( new InterpolationPoint( 0.511806375442739 , 0.2916067695915534 ) );
		interps.add( new InterpolationPoint( 0.5135773317591499 , 0.2879208549217125 ) );
		interps.add( new InterpolationPoint( 0.5153482880755607 , 0.27917173857629446 ) );
		interps.add( new InterpolationPoint( 0.5171192443919717 , 0.27448313595108587 ) );
		interps.add( new InterpolationPoint( 0.5188902007083825 , 0.27841995332868696 ) );
		interps.add( new InterpolationPoint( 0.5206611570247933 , 0.27416399275376835 ) );
		interps.add( new InterpolationPoint( 0.5224321133412042 , 0.2671228876831464 ) );
		interps.add( new InterpolationPoint( 0.524203069657615 , 0.26252892265581423 ) );
		interps.add( new InterpolationPoint( 0.525974025974026 , 0.258367008672025 ) );
		interps.add( new InterpolationPoint( 0.5277449822904368 , 0.25482285457442255 ) );
		interps.add( new InterpolationPoint( 0.5295159386068476 , 0.25466112624623394 ) );
		interps.add( new InterpolationPoint( 0.5312868949232585 , 0.2471831936433592 ) );
		interps.add( new InterpolationPoint( 0.5330578512396693 , 0.2382744677055608 ) );
		interps.add( new InterpolationPoint( 0.5348288075560802 , 0.23627103445497585 ) );
		interps.add( new InterpolationPoint( 0.5365997638724911 , 0.23990194729369999 ) );
		interps.add( new InterpolationPoint( 0.5383707201889019 , 0.23155214811335426 ) );
		interps.add( new InterpolationPoint( 0.5401416765053129 , 0.21834982652448987 ) );
		interps.add( new InterpolationPoint( 0.5419126328217236 , 0.22031935801216623 ) );
		interps.add( new InterpolationPoint( 0.5436835891381345 , 0.22607607478290195 ) );
		interps.add( new InterpolationPoint( 0.5454545454545454 , 0.2155299894417017 ) );
		interps.add( new InterpolationPoint( 0.5472255017709562 , 0.20176331586339957 ) );
		interps.add( new InterpolationPoint( 0.5489964580873672 , 0.2033447967500735 ) );
		interps.add( new InterpolationPoint( 0.550767414403778 , 0.20903323894761594 ) );
		interps.add( new InterpolationPoint( 0.5525383707201889 , 0.20146057791978128 ) );
		interps.add( new InterpolationPoint( 0.5543093270365997 , 0.18799869212693351 ) );
		interps.add( new InterpolationPoint( 0.5560802833530105 , 0.18885550790840847 ) );
		interps.add( new InterpolationPoint( 0.5578512396694214 , 0.1926433516347445 ) );
		interps.add( new InterpolationPoint( 0.5596221959858323 , 0.19318273415822304 ) );
		interps.add( new InterpolationPoint( 0.5613931523022431 , 0.18466137462455479 ) );
		interps.add( new InterpolationPoint( 0.5631641086186541 , 0.17889055639781087 ) );
		interps.add( new InterpolationPoint( 0.5649350649350648 , 0.1828883381549574 ) );
		interps.add( new InterpolationPoint( 0.5667060212514757 , 0.1906388205303268 ) );
		interps.add( new InterpolationPoint( 0.5684769775678866 , 0.18612452336280877 ) );
		interps.add( new InterpolationPoint( 0.5702479338842974 , 0.17639765954486458 ) );
		interps.add( new InterpolationPoint( 0.5720188902007084 , 0.1794746829344513 ) );
		interps.add( new InterpolationPoint( 0.5737898465171192 , 0.1924542103896628 ) );
		interps.add( new InterpolationPoint( 0.5755608028335301 , 0.18960047015431905 ) );
		interps.add( new InterpolationPoint( 0.577331759149941 , 0.1779747040950722 ) );
		interps.add( new InterpolationPoint( 0.5791027154663517 , 0.18180619265003325 ) );
		interps.add( new InterpolationPoint( 0.5808736717827626 , 0.19489903243176068 ) );
		interps.add( new InterpolationPoint( 0.5826446280991735 , 0.19461485834890932 ) );
		interps.add( new InterpolationPoint( 0.5844155844155843 , 0.18239152122608762 ) );
		interps.add( new InterpolationPoint( 0.5861865407319953 , 0.1839343761818938 ) );
		interps.add( new InterpolationPoint( 0.587957497048406 , 0.1950367019642888 ) );
		interps.add( new InterpolationPoint( 0.5897284533648169 , 0.198182268862283 ) );
		interps.add( new InterpolationPoint( 0.5914994096812278 , 0.18955120758779093 ) );
		interps.add( new InterpolationPoint( 0.5932703659976386 , 0.18665279971763116 ) );
		interps.add( new InterpolationPoint( 0.5950413223140496 , 0.19237824347112642 ) );
		interps.add( new InterpolationPoint( 0.5968122786304604 , 0.2000968391776339 ) );
		interps.add( new InterpolationPoint( 0.5985832349468713 , 0.19768584164932435 ) );
		interps.add( new InterpolationPoint( 0.6003541912632822 , 0.189861791865465 ) );
		interps.add( new InterpolationPoint( 0.6021251475796929 , 0.19017604942112476 ) );
		interps.add( new InterpolationPoint( 0.6038961038961038 , 0.2022896629440362 ) );
		interps.add( new InterpolationPoint( 0.6056670602125147 , 0.20407993247741774 ) );
		interps.add( new InterpolationPoint( 0.6074380165289255 , 0.19272524943785652 ) );
		interps.add( new InterpolationPoint( 0.6092089728453365 , 0.18781010349250982 ) );
		interps.add( new InterpolationPoint( 0.6109799291617473 , 0.20005371310232445 ) );
		interps.add( new InterpolationPoint( 0.6127508854781581 , 0.20264516798646554 ) );
		interps.add( new InterpolationPoint( 0.6145218417945689 , 0.18789551462856116 ) );
		interps.add( new InterpolationPoint( 0.6162927981109798 , 0.1829246337834256 ) );
		interps.add( new InterpolationPoint( 0.6180637544273908 , 0.1917109470834057 ) );
		interps.add( new InterpolationPoint( 0.6198347107438016 , 0.19471707069626723 ) );
		interps.add( new InterpolationPoint( 0.6216056670602125 , 0.18159978529443582 ) );
		interps.add( new InterpolationPoint( 0.6233766233766234 , 0.1745044129908817 ) );
		interps.add( new InterpolationPoint( 0.6251475796930341 , 0.17950759889002885 ) );
		interps.add( new InterpolationPoint( 0.626918536009445 , 0.18357918653086652 ) );
		interps.add( new InterpolationPoint( 0.6286894923258559 , 0.17590455422176018 ) );
		interps.add( new InterpolationPoint( 0.6304604486422667 , 0.16690584160399008 ) );
		interps.add( new InterpolationPoint( 0.6322314049586777 , 0.16536400023986284 ) );
		interps.add( new InterpolationPoint( 0.6340023612750885 , 0.17270431107100223 ) );
		interps.add( new InterpolationPoint( 0.6357733175914994 , 0.17105302879848117 ) );
		interps.add( new InterpolationPoint( 0.6375442739079101 , 0.15984016052040304 ) );
		interps.add( new InterpolationPoint( 0.639315230224321 , 0.15435154753608046 ) );
		interps.add( new InterpolationPoint( 0.641086186540732 , 0.16165652909520745 ) );
		interps.add( new InterpolationPoint( 0.6428571428571428 , 0.16548243893193268 ) );
		interps.add( new InterpolationPoint( 0.6446280991735536 , 0.1540681643025565 ) );
		interps.add( new InterpolationPoint( 0.6463990554899646 , 0.14639807406010136 ) );
		interps.add( new InterpolationPoint( 0.6481700118063753 , 0.15361880883055692 ) );
		interps.add( new InterpolationPoint( 0.6499409681227862 , 0.15788435013298238 ) );
		interps.add( new InterpolationPoint( 0.651711924439197 , 0.14848920952132633 ) );
		interps.add( new InterpolationPoint( 0.6534828807556079 , 0.14290436992950234 ) );
		interps.add( new InterpolationPoint( 0.6552538370720187 , 0.14804412267378636 ) );
		interps.add( new InterpolationPoint( 0.6570247933884297 , 0.15183977524159378 ) );
		interps.add( new InterpolationPoint( 0.6587957497048406 , 0.14652945135716638 ) );
		interps.add( new InterpolationPoint( 0.6605667060212513 , 0.1429391122639397 ) );
		interps.add( new InterpolationPoint( 0.6623376623376622 , 0.14584746101378068 ) );
		interps.add( new InterpolationPoint( 0.6641086186540732 , 0.1483865548894779 ) );
		interps.add( new InterpolationPoint( 0.665879574970484 , 0.14859234283896333 ) );
		interps.add( new InterpolationPoint( 0.6676505312868949 , 0.1482140905460812 ) );
		interps.add( new InterpolationPoint( 0.6694214876033057 , 0.14656959700066716 ) );
		interps.add( new InterpolationPoint( 0.6711924439197166 , 0.14911768780601448 ) );
		interps.add( new InterpolationPoint( 0.6729634002361276 , 0.15405206205807057 ) );
		interps.add( new InterpolationPoint( 0.6747343565525383 , 0.15431493199854399 ) );
		interps.add( new InterpolationPoint( 0.6765053128689492 , 0.1508427546736929 ) );
		interps.add( new InterpolationPoint( 0.67827626918536 , 0.15147071973229864 ) );
		interps.add( new InterpolationPoint( 0.6800472255017709 , 0.15741565570256996 ) );
		interps.add( new InterpolationPoint( 0.6818181818181819 , 0.15866854296389654 ) );
		interps.add( new InterpolationPoint( 0.6835891381345927 , 0.15507996054022016 ) );
		interps.add( new InterpolationPoint( 0.6853600944510035 , 0.15510342071160177 ) );
		interps.add( new InterpolationPoint( 0.6871310507674143 , 0.1580378161231627 ) );
		interps.add( new InterpolationPoint( 0.6889020070838252 , 0.15943916408951977 ) );
		interps.add( new InterpolationPoint( 0.690672963400236 , 0.16000952194494678 ) );
		interps.add( new InterpolationPoint( 0.692443919716647 , 0.1576953328670502 ) );
		interps.add( new InterpolationPoint( 0.6942148760330578 , 0.15588893499442083 ) );
		interps.add( new InterpolationPoint( 0.6959858323494686 , 0.15856580876320764 ) );
		interps.add( new InterpolationPoint( 0.6977567886658794 , 0.16196681878657498 ) );
		interps.add( new InterpolationPoint( 0.6995277449822903 , 0.15766443576171568 ) );
		interps.add( new InterpolationPoint( 0.7012987012987011 , 0.15216385906992386 ) );
		interps.add( new InterpolationPoint( 0.7030696576151121 , 0.15651627046434224 ) );
		interps.add( new InterpolationPoint( 0.704840613931523 , 0.1617040798694373 ) );
		interps.add( new InterpolationPoint( 0.7066115702479338 , 0.1546408470617645 ) );
		interps.add( new InterpolationPoint( 0.7083825265643446 , 0.14602519723472673 ) );
		interps.add( new InterpolationPoint( 0.7101534828807554 , 0.14970731894621903 ) );
		interps.add( new InterpolationPoint( 0.7119244391971664 , 0.15520706798098047 ) );
		interps.add( new InterpolationPoint( 0.7136953955135773 , 0.14695878998117615 ) );
		interps.add( new InterpolationPoint( 0.7154663518299881 , 0.13680575915348583 ) );
		interps.add( new InterpolationPoint( 0.717237308146399 , 0.13932036496137906 ) );
		interps.add( new InterpolationPoint( 0.7190082644628097 , 0.14300115706083918 ) );
		interps.add( new InterpolationPoint( 0.7207792207792207 , 0.13796788773713128 ) );
		interps.add( new InterpolationPoint( 0.7225501770956316 , 0.1282719842846338 ) );
		interps.add( new InterpolationPoint( 0.7243211334120424 , 0.12606294286160122 ) );
		interps.add( new InterpolationPoint( 0.7260920897284533 , 0.1279520615395617 ) );
		interps.add( new InterpolationPoint( 0.7278630460448641 , 0.1271361828858751 ) );
		interps.add( new InterpolationPoint( 0.7296340023612751 , 0.11965565012643228 ) );
		interps.add( new InterpolationPoint( 0.731404958677686 , 0.11403587729060291 ) );
		interps.add( new InterpolationPoint( 0.7331759149940967 , 0.11457175461600491 ) );
		interps.add( new InterpolationPoint( 0.7349468713105076 , 0.11823800406631635 ) );
		interps.add( new InterpolationPoint( 0.7367178276269186 , 0.11288273148635401 ) );
		interps.add( new InterpolationPoint( 0.7384887839433294 , 0.10406842169965769 ) );
		interps.add( new InterpolationPoint( 0.7402597402597403 , 0.10512415502154439 ) );
		interps.add( new InterpolationPoint( 0.7420306965761511 , 0.11031126678779046 ) );
		interps.add( new InterpolationPoint( 0.743801652892562 , 0.10662411976055014 ) );
		interps.add( new InterpolationPoint( 0.7491145218417945 , 0.10383415931878248 ) );
		interps.add( new InterpolationPoint( 0.7508854781582054 , 0.10209605828689783 ) );
		interps.add( new InterpolationPoint( 0.7579693034238488 , 0.10089883899835367 ) );
		interps.add( new InterpolationPoint( 0.7650531286894922 , 0.10299553938010451 ) );
		interps.add( new InterpolationPoint( 0.7668240850059032 , 0.1041412216636979 ) );
		interps.add( new InterpolationPoint( 0.7721369539551357 , 0.10797431531862556 ) );
		interps.add( new InterpolationPoint( 0.7739079102715465 , 0.11111024436064246 ) );
		interps.add( new InterpolationPoint( 0.7756788665879575 , 0.10487730743153427 ) );
		interps.add( new InterpolationPoint( 0.7774498229043684 , 0.10396208781900747 ) );
		interps.add( new InterpolationPoint( 0.7792207792207791 , 0.11161427601857662 ) );
		interps.add( new InterpolationPoint( 0.78099173553719 , 0.11581048531987352 ) );
		interps.add( new InterpolationPoint( 0.7827626918536008 , 0.11006431014641027 ) );
		interps.add( new InterpolationPoint( 0.7845336481700118 , 0.10794373194515856 ) );
		interps.add( new InterpolationPoint( 0.7863046044864227 , 0.11361627438403117 ) );
		interps.add( new InterpolationPoint( 0.7880755608028335 , 0.11796939489743505 ) );
		interps.add( new InterpolationPoint( 0.7898465171192444 , 0.11461713439528937 ) );
		interps.add( new InterpolationPoint( 0.7916174734356551 , 0.11143198409652093 ) );
		interps.add( new InterpolationPoint( 0.7933884297520661 , 0.1125348343378952 ) );
		interps.add( new InterpolationPoint( 0.7951593860684769 , 0.11782113795885558 ) );
		interps.add( new InterpolationPoint( 0.7969303423848878 , 0.11817080773192555 ) );
		interps.add( new InterpolationPoint( 0.7987012987012986 , 0.1125366229309615 ) );
		interps.add( new InterpolationPoint( 0.8004722550177095 , 0.11019801852457935 ) );
		interps.add( new InterpolationPoint( 0.8022432113341202 , 0.11572975340500322 ) );
		interps.add( new InterpolationPoint( 0.8040141676505312 , 0.1182832503175471 ) );
		interps.add( new InterpolationPoint( 0.805785123966942 , 0.11182774256021574 ) );
		interps.add( new InterpolationPoint( 0.8075560802833529 , 0.10609227455685537 ) );
		interps.add( new InterpolationPoint( 0.8093270365997637 , 0.1111142369123179 ) );
		interps.add( new InterpolationPoint( 0.8110979929161746 , 0.11394265525397304 ) );
		interps.add( new InterpolationPoint( 0.8128689492325856 , 0.10661389469137073 ) );
		interps.add( new InterpolationPoint( 0.8146399055489963 , 0.10093524976365706 ) );
		interps.add( new InterpolationPoint( 0.8164108618654072 , 0.1031393967006172 ) );
		interps.add( new InterpolationPoint( 0.8181818181818181 , 0.10514439845483937 ) );
		interps.add( new InterpolationPoint( 0.8277511961722488 , 0.1002548101911991 ) );
		interps.add( new InterpolationPoint( 0.8373205741626795 , 0.09467586001810703 ) );
		interps.add( new InterpolationPoint( 0.8468899521531099 , 0.08851656616577046 ) );
		interps.add( new InterpolationPoint( 0.8564593301435406 , 0.08188594686439603 ) );
		interps.add( new InterpolationPoint( 0.8660287081339713 , 0.07489302034419078 ) );
		interps.add( new InterpolationPoint( 0.8755980861244019 , 0.06764680483536184 ) );
		interps.add( new InterpolationPoint( 0.8851674641148325 , 0.06025631856811615 ) );
		interps.add( new InterpolationPoint( 0.8947368421052633 , 0.05283057977266053 ) );
		interps.add( new InterpolationPoint( 0.9043062200956937 , 0.045478606679202324 ) );
		interps.add( new InterpolationPoint( 0.9138755980861244 , 0.03830941751794807 ) );
		interps.add( new InterpolationPoint( 0.923444976076555 , 0.03143203051910515 ) );
		interps.add( new InterpolationPoint( 0.9330143540669856 , 0.02495546391288018 ) );
		interps.add( new InterpolationPoint( 0.9425837320574163 , 0.018988735929480405 ) );
		interps.add( new InterpolationPoint( 0.9521531100478469 , 0.013640864799112716 ) );
		interps.add( new InterpolationPoint( 0.9617224880382776 , 0.009020868751984046 ) );
		interps.add( new InterpolationPoint( 0.9712918660287082 , 0.005237766018301494 ) );
		interps.add( new InterpolationPoint( 0.9808612440191387 , 0.002400574828272021 ) );
		interps.add( new InterpolationPoint( 0.9904306220095692 , 6.183134121025102E-4 ) );
		interps.add( new InterpolationPoint( 1.0 , 0.0 ) );
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
	public static void buildBend( NoteDesc desc , NoteDesc nxt , double minDecayTimeBeats )
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
		
		if( SongData.ROUGH_DRAFT_MODE )
		{
			SongData.buildBendInterpPoints(note1,note2,10,minDecayTimeBeats,false,core);
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
		
		if( SongData.ROUGH_DRAFT_MODE )
		{
			SongData.buildBendInterpPoints(note1,null,10,minDecayTimeBeats,false,core);
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
		bezAC.getInterpolationPoints().add(new InterpolationPoint(36.0, dvalPrim(0.82)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(52.0, dvalPrim(0.84)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(53.0, dvalPrim(0.84)));

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
		bezAC.getInterpolationPoints().add(new InterpolationPoint(36.0, dvalSec(0.15)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(52.0, dvalSec(0.13)));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(53.0, dvalSec(0.13)));

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
		final double maxDivisor = 5.00;
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.45363408521303256 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.3057644110275689 );
		PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.3533834586466165 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.15538847117794485 );
		PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.47117794486215536 );
		PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16040100250626566 );
		/* PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.17293233082706766 );
		PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		PhaseDistortionPacket[] pdccxj = { pdcxj };
		PhaseDistortionPacket[] pdccxk = { pdcxk };
		/* PhaseDistortionPacket[] pdccxl = { pdcxl };
		PhaseDistortionPacket[] pdccxm = { pdcxm }; */
		WaveForm invf = new SinglePacketInverter(pdcxf);
		WaveForm invg = new SinglePacketInverter(pdcxg);
		WaveForm invh = new SinglePacketInverter(pdcxh);
		WaveForm invi = new SinglePacketInverter(pdcxi);
		WaveForm invj = new SinglePacketInverter(pdcxj);
		WaveForm invk = new SinglePacketInverter(pdcxk);
		/* WaveForm invl = new Inverter(pdccxl);
		WaveForm invm = new Inverter(pdccxm); */
		NonClampedCoefficient primaryCoeff = new ConstantNonClampedCoefficient( 1.0 / maxDivisor );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		// coefficients.add( invg );
		// coefficientCoefficients.add( new ConstantNonClampedCoefficient( -1.0 / maxDivisor ));
		// parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invh );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.810126582278481 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invi );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.22784810126582278 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.0379746835443038 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.4936708860759494 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		WaveForm wave3 = new AdditiveWaveForm( invf , primaryCoeff ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
		return( wave3 );
	}
	
	
	/**
	 * Gets the initial waveform for the primary timbre.
	 * @return The waveform for the primary timbre.
	 */
	protected WaveForm genWaveA( )
	{
		final double maxDivisor = 2.68;
		
		ArrayList<NonClampedCoefficient> coefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		ArrayList<NonClampedCoefficient> parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.5789473684210527 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.19799498746867167 );
		PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.9097744360902256 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.40852130325814534 );
		PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.22807017543859648 );
		PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.20300751879699247 );
		PhaseDistortionPacket pdcxl = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.2731829573934837 );
		/* PhaseDistortionPacket pdcxm = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16290726817042606 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		PhaseDistortionPacket[] pdccxj = { pdcxj };
		PhaseDistortionPacket[] pdccxk = { pdcxk };
		PhaseDistortionPacket[] pdccxl = { pdcxl };
		/* PhaseDistortionPacket[] pdccxm = { pdcxm }; */
		WaveForm invf = new Inverter(pdccxf);
		WaveForm invg = new SinglePacketInverter(pdcxg);
		WaveForm invh = new SinglePacketInverter(pdcxh);
		WaveForm invi = new SinglePacketInverter(pdcxi);
		WaveForm invj = new SinglePacketInverter(pdcxj);
		WaveForm invk = new SinglePacketInverter(pdcxk);
		WaveForm invl = new SinglePacketInverter(pdcxl);
		/* WaveForm invm = new SinglePacketInverter(pdccxm); */
		NonClampedCoefficient primaryCoeff = new ConstantNonClampedCoefficient( -0.9493670886075949 / maxDivisor );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		coefficients.add( invg );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invh );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.02531645569620253 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		coefficients.add( invi );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.012658227848101266 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.02531645569620253 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invl );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.012658227848101266 / maxDivisor ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invm );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.5189873417721519 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		WaveForm wave3 = new AdditiveWaveForm( invf , primaryCoeff ,
				coefficients , coefficientCoefficients , parameterCoefficients );
		
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
		
		
		PhaseDistortionPacket pdcxf = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.5789473684210527 );
		PhaseDistortionPacket pdcxg = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.19799498746867167 );
		PhaseDistortionPacket pdcxh = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.9097744360902256 );
		PhaseDistortionPacket pdcxi = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.40852130325814534 );
		/* PhaseDistortionPacket pdcxj = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.47117794486215536 );
		PhaseDistortionPacket pdcxk = new PhaseDistortionPacket(
				new SineWaveform(), 1.0, 0.16040100250626566 ); */
		PhaseDistortionPacket[] pdccxf = { pdcxf };
		PhaseDistortionPacket[] pdccxg = { pdcxg };
		PhaseDistortionPacket[] pdccxh = { pdcxh };
		PhaseDistortionPacket[] pdccxi = { pdcxi };
		/* PhaseDistortionPacket[] pdccxj = { pdcxj };
		PhaseDistortionPacket[] pdccxk = { pdcxk }; */
		WaveForm invf = new SinglePacketInverter(pdcxf);
		WaveForm invg = new SinglePacketInverter(pdcxg);
		WaveForm invh = new SinglePacketInverter(pdcxh);
		WaveForm invi = new SinglePacketInverter(pdcxi);
		/* WaveForm invj = new SinglePacketInverter(pdccxj);
		WaveForm invk = new SinglePacketInverter(pdccxk); */
		primaryCoeff = new ConstantNonClampedCoefficient( -0.9493670886075949 );
		coefficients = new ArrayList<NonClampedCoefficient>();
		coefficientCoefficients = new ArrayList<NonClampedCoefficient>();
		parameterCoefficients = new ArrayList<NonClampedCoefficient>();
		coefficients.add( invg );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invh );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.02531645569620253 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		coefficients.add( invi );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.012658227848101266 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		/* coefficients.add( invj );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( -0.0379746835443038 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 ));
		coefficients.add( invk );
		coefficientCoefficients.add( new ConstantNonClampedCoefficient( 0.02531645569620253 ));
		parameterCoefficients.add( new ConstantNonClampedCoefficient( 1.0 )); */
		
		// wave3 = new AdditiveWaveForm( invf , primaryCoeff ,
		//		coefficients , coefficientCoefficients , parameterCoefficients );
		
		// note.setWaveform(wave3);
		
		
		//note.setRoughDraftWaveform(new SineWaveform());
		
		note.setTotalEnvelopeMode( NoteDesc.TOTAL_ENVELOPE_MODE_NONE );
	}

	/**
	 * Puts a phase distortion for the initial instrument "pluck" (or equivalent thereof) on top of the default timbre.
	 * @param wave0 The input default timbre
	 * @return The phase-distorted version of the timbre
	 */
	public static GWaveForm buildBassWaveFormB(GWaveForm wave0) {
		PiecewiseCubicMonotoneBezierFlat bezAC = new PiecewiseCubicMonotoneBezierFlat();
		bezAC.getInterpolationPoints().add(new InterpolationPoint(0.0, -8+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(1.0, -4+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(2.0, -2+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(3.0, -1+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(4.0, -0.5+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(5.0, 0.0+8));
		bezAC.getInterpolationPoints().add(new InterpolationPoint(6.0, 0.0+8));
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
	public MidAgentMoheNoDistort() {
		super();
		// vibratoParams.setPitchSlide( 0.0 );
		
		WaveForm inva = genWaveB();
		WaveForm invb = genWaveA();
		
		GWaveForm ina = new GRoughDraftWaveSwitch( inva.genWave( new HashMap() ) , new GSawtoothWaveform() );
		GWaveForm inb = new GRoughDraftWaveSwitch( invb.genWave( new HashMap() ) , new GSawtoothWaveform() );
		
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
		// Harmony : 98.10625
		NoteInitializer fa = new NoteInitializer( 114.46875 );
		noteInitializers.clear();
		noteInitializers.add( fa );
		noteInitializers.add( fa );
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
