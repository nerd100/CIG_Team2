import java.io.*;
import java.util.Random;
import java.util.*;

import structs.FrameData;
import structs.GameData;
import structs.Key;
import gameInterface.AIInterface;
import commandcenter.CommandCenter;

public class Ryu implements AIInterface {
	BufferedWriter bw = null;
	BufferedWriter bw2 = null;
	File file;
	File file2;
	FileWriter fw;
	FileWriter fw2;
	
	String tempskill;
	String count = null;
	
	int time = 0;
	int enemyHealth;
	int absDamage = 0;
	int currentDmg = 0;
	int lastDmg = 0;
	int fightLine = -1;
	int line = 0;
	
	boolean nextSkill;
	boolean CmdExist = false;
	boolean playerNumber;
	boolean limit = false;
	
	Key inputKey;
	FrameData  frameData;
	CommandCenter cc;
	
	List <String> succList = new ArrayList<String>();
	List <String> fightList = new ArrayList<String>();
	
	String[] fightArray;
	String[] skills = 
		{"AIR","AIR_A","AIR_B","AIR_D_DB_BA","AIR_D_DB_BB","AIR_D_DF_FA","AIR_D_DF_FB"
			+ "AIR_DA","AIR_DB","AIR_F_D_DFA","AIR_F_D_DFB","AIR_FA","AIR_FB","AIR_GUARD","AIR_GUARD_RECOV",
			"AIR_RECOV","AIR_UA","AIR_UB","BACK_JUMP", "BACK_STEP","CHANGE_DOWN",
			"CROUCH","CROUCH_A","CROUCH_B","CROUCH_FA","CROUCH_FB","CROUCH_GUARD","CROUCH_GUARD_RECOV",
			"CROUCH_RECOV","DASH","DOWN","FOR_JUMP","FORWARD_WALK","JUMP","LANDING","NEUTRAL","RISE",
			"STAND","STAND_A","STAND_B","STAND_D_DB_BA","STAND_D_DB_BB","STAND_D_DF_FA","STAND_D_DF_FB",
			"STAND_D_DF_FC","STAND_F_D_DFA","STAND_F_D_DFB","STAND_FA","STAND_FB","STAND_GUARD",  
			"STAND_GUARD_RECOV","STAND_RECOV","THROW_A","THROW_B","THROW_HIT","THROW_SUFFER"};
	
	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		this.playerNumber = playerNumber;
		this.inputKey = new Key();
		cc = new CommandCenter();
		frameData = new FrameData();
		enemyHealth = 0;
		nextSkill = true;
		//create file
		file = new File("data/aiData/SkillTable/skill_table.txt");
		file2 = new File("data/aiData/SkillTable/Round.txt");
		
		try {
			if(!file.exists())
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		file2.getParentFile().mkdirs();
		try {
			if(!file2.exists())
			file2.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		try {
			fw = new FileWriter(file.getAbsoluteFile(),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bw = new BufferedWriter(fw);
		
		reset();
		
	    return 0; 
	}


	@Override
	public  void getInformation(FrameData frameData)
	{  this.frameData = frameData;
	cc.setFrameData(this.frameData,playerNumber);
	} 

	
	@Override
	public void processing(){
		if(frameData.getRound() <= 0){
			
		if (time > 100){
			tempskill = rndcommand(skills);
			System.out.println(tempskill);
			time = 0;
		}

		fight(tempskill);
		fillList();
		
		if(succList.size() == 5)
		{
			succList.add(Integer.toString(absDamage));
			try {
				bw.append(succList.toString());
				bw.newLine();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("write in file");
			succList.removeAll(succList);
			absDamage = 0;
		}
	time++;
		}else{
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new FileReader(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      List<String> results = new ArrayList<String>();
		      String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		      while (line != null) {
		          results.add(line);
		          try {
					line = reader.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      }
		      //System.out.println(results);
		      if(fightLine == -1){
		      fightLine = whichLine(results);
		      }
		      //System.out.println(results.get(fightLine));
		      if(fightList.isEmpty()){
		      String temp = results.get(fightLine);
		      String[] temp1 = temp.split(",");
		      for (int j = 0; j < temp1.length-1;j++)
		      	{
		    
		    	  temp1[j] = temp1[j].replaceAll("\\s|\\[|\\]", "");
	    		  fightList.add(temp1[j]);
	    	  	}
		      fightList.add("FORWARD_WALK");
		      fightArray = fightList.toArray(new String[fightList.size()]);
		      }

		      tempskill = rndcommand(fightArray);
		      System.out.println(fightList);
		      fight(tempskill);
		}
	}
	
	public int whichLine(List<String> results)
	{
		
		for (int i = 0 ; i < results.size(); i++)
	      {
	    	  //System.out.println(results.get(i));
	    	  String temp = results.get(i);
	    	  String[] temp1 = temp.split(",");
	    	  List <String> temp2 = new ArrayList<String>();
	    	  for (int j = 0; j < temp1.length;j++)
	    	  {
	    		  temp2.add(temp1[j]);
	    	  }
	    	  String dmg = temp2.get(5).replaceAll("[\\s\\]]", "");
	    	  currentDmg = Integer.parseInt(dmg);
	    	  if(lastDmg < currentDmg){
	    	  lastDmg = currentDmg;
	    	  line = i;
	    	  }
	      }   	
		return line;
	}
	
	public void fillList()
	{
		if(!frameData.getEmptyFlag()){
			if (enemyHealth > cc.getEnemyHP())
			{	
				for(int i = 0; i < succList.size();i++)
				{
					if(succList.get(i) == tempskill)
					{
						CmdExist = true;
						break;
					}
				}
				if(CmdExist == false){
				succList.add(tempskill);
				absDamage += Math.abs(enemyHealth - cc.getEnemyHP());
				}
				CmdExist = false;
				enemyHealth = cc.getEnemyHP();
			}
		
		}
	}
	
	public void reset()
	{
		BufferedReader reader2 = null;
		try {
			reader2 = new BufferedReader(new FileReader(file2));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			count = reader2.readLine();
			System.out.println(count);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			fw2 = new FileWriter(file2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		bw2 = new BufferedWriter(fw2);
		
		
		if(count != null)
		{
			if (Integer.parseInt(count) >= 11)
			{
				try {
					PrintWriter writer = new PrintWriter(file);
					writer.print("");
					writer.close();

				} catch (IOException e2) {
					e2.printStackTrace();
				}
				limit = true;
				try {
					file.createNewFile();
				} catch (IOException e1) {

					e1.printStackTrace();
				}
				try {
					bw2.write("0");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (limit == false){
				int f = Integer.parseInt(count);
				f+=1;
				try {
					bw2.write(Integer.toString(f));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		}else{
			try {
				bw2.write("0");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		try {
			bw2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void fight(String tempskill){
		if(!frameData.getEmptyFlag() && frameData.getRemainingTime() > 0 && tempskill != null){
			  if(cc.getskillFlag())
			  	{inputKey = cc.getSkillKey();
			  	}else{
			  		inputKey.empty(); 
			  		cc.skillCancel();
			  		cc.commandCall(tempskill);  
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
		try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public String getCharacter() {
		// TODO Auto-generated method stub
		return CHARACTER_ZEN;
	}
	
}
