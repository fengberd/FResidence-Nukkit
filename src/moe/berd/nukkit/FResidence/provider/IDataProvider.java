package moe.berd.nukkit.FResidence.provider;

import java.util.*;

import cn.nukkit.level.*;

import moe.berd.nukkit.FResidence.utils.*;

public interface IDataProvider
{
	int addResidence(Position pos1,Position pos2,Object owner,String name);
	
	
	Residence getResidence(int id);
	
	Residence getResidenceByName(String name);
	
	Residence getResidenceByPosition(Position pos);
	
	
	Map<Integer,Residence> getAllResidences();
	
	Residence[] getResidencesByOwner(Object owner);
	
	
	boolean removeResidence(Object id);
	
	int removeResidencesByOwner(Object owner);
	
	
	IDataProvider save();
	
	IDataProvider close(); // save=true
	IDataProvider close(boolean save);
	
	IDataProvider reload(); // save=false
	IDataProvider reload(boolean save);
	
	
	String getName();
}
