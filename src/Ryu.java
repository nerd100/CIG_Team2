import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.*;

import structs.FrameData;
import structs.GameData;
import structs.Key;
import gameInterface.AIInterface;
import commandcenter.CommandCenter;

public class Ryu implements AIInterface {

	int time = 0;
	int enemyHealth;
	boolean nextSkill;
	File file;
	Key inputKey;
	String tempskill;
	boolean playerNumber;
	FrameData  frameData;
	CommandCenter cc;
	
	String[] skills = 
		{"AIR","AIR_A","AIR_B","AIR_D_DB_BA","AIR_D_DB_BB","AIR_D_DF_FA","AIR_D_DF_FB"
			+ "AIR_DA","AIR_DB","AIR_F_D_DFA","AIR_F_D_DFB","AIR_FA","AIR_FB","AIR_GUARD","AIR_GUARD_RECOV",
			"AIR_RECOV","AIR_UA","AIR_UB","BACK_JUMP", "BACK_STEP","CHANGE_DOWN",
			"CROUCH","CROUCH_A","CROUCH_B","CROUCH_FA","CROUCH_FB","CROUCH_GUARD","CROUCH_GUARD_RECOV",
			"CROUCH_RECOV","DASH","DOWN","FOR_JUMP","FORWARD_WALK","JUMP","LANDING","NEUTRAL","RISE",
			"STAND","STAND_A","STAND_B","STAND_D_DB_BA","STAND_D_DB_BB","STAND_D_DF_FA","STAND_D_DF_FB",
			"STAND_D_DF_FC","STAND_F_D_DFA","STAND_F_D_DFB","STAND_FA","STAND_FB","STAND_GUARD",  
			"STAND_GUARD_RECOV","STAND_RECOV","THROW_A","THROW_B","THROW_HIT","THROW_SUFFER"};
	
	List <String> succList = new ArrayList<String>();
	
	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		enemyHealth = 0;
		nextSkill = true;
		//create file
		file = new File("data/aiData/SkillTable/skill_table.txt");
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//create file
		this.playerNumber = playerNumber;
		this.inputKey = new Key();
		cc = new CommandCenter();
		frameData = new FrameData();
		return 0; 
	}


	@Override
	public  void getInformation(FrameData frameData)
	{  this.frameData = frameData;
	cc.setFrameData(this.frameData,playerNumber);
	} 

	
	@Override
	public void processing(){ 
		if (time > 50){
			tempskill = rndcommand(skills);
			System.out.println(tempskill);
			time = 0;
		}

		
	if(!frameData.getEmptyFlag() && frameData.getRemainingTime() > 0 && tempskill != null){
	  if(cc.getskillFlag())
	  	{inputKey = cc.getSkillKey();
	  	}else{
	  		inputKey.empty(); 
	  		cc.skillCancel();
	  		cc.commandCall(tempskill);  
	  		
		} 
	} 
	time++;
	if(!frameData.getEmptyFlag()){
			if (enemyHealth > cc.getEnemyHP())
			{
				succList.add(tempskill);
				enemyHealth = cc.getEnemyHP();
			}
		
		}
	}
	public String rndcommand(String[] commandlist){
		int rnd = new Random().nextInt(commandlist.length);
		return commandlist[rnd];
	}

	@Override
	public Key input() {
		// TODO Auto-generated method stub
		return inputKey;
	}

	public void close(){
		System.out.println(succList);
	}
	

	@Override
	public String getCharacter() {
		// TODO Auto-generated method stub
		return CHARACTER_ZEN;
	}
	
}
