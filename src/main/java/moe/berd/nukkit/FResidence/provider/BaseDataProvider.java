package moe.berd.nukkit.FResidence.provider;

import java.util.*;

import cn.nukkit.level.*;
import cn.nukkit.utils.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.utils.*;

public abstract class BaseDataProvider implements IDataProvider
{
	protected Main main;
	protected Map<Integer,Residence> residences=new HashMap<>();
	
	protected List<ConfigSection> failed=new ArrayList<>();
	
	public BaseDataProvider(Main main)
	{
		this.main=main;
		reload();
	}
	
	@Override
	public int addResidence(Position pos1,Position pos2,Object owner,String name)
	{
		int id=residences.size();
		residences.put(id,new Residence(this,id,name,owner,pos1,pos2));
		save();
		return id;
	}
	
	@Override
	public Residence getResidence(int id)
	{
		return residences.getOrDefault(id,null);
	}
	
	@Override
	public Residence getResidenceByName(String name)
	{
		for(Residence res : residences.values())
		{
			if(res.getName().equals(name))
			{
				return res;
			}
		}
		return null;
	}
	
	@Override
	public Residence getResidenceByPosition(Position pos)
	{
		for(Residence res : residences.values())
		{
			if(res.inResidence(pos))
			{
				return res;
			}
		}
		return null;
	}
	
	@Override
	public Map<Integer,Residence> getAllResidences()
	{
		return residences;
	}
	
	@Override
	public Residence[] getResidencesByOwner(Object owner)
	{
		List<Residence> result=new ArrayList<>();
		for(Residence res : residences.values())
		{
			if(res.isOwner(owner))
			{
				result.add(res);
			}
		}
		return result.toArray(new Residence[result.size()]);
	}
	
	@Override
	public boolean removeResidence(Object val)
	{
		int id=0;
		if(val instanceof Residence)
		{
			id=((Residence)val).getId();
		}
		if(!(val instanceof Integer))
		{
			throw new IllegalArgumentException("Invalid val,this function only accepts Residence/int.");
		}
		id=(int)val;
		if(!residences.containsKey(id))
		{
			return false;
		}
		residences.remove(id);
		save();
		return true;
	}
	
	@Override
	public int removeResidencesByOwner(Object owner)
	{
		List<Integer> remove=new ArrayList<>();
		residences.keySet().forEach(id->
		{
			if(residences.get(id).isOwner(owner))
			{
				remove.add(id);
			}
		});
		remove.forEach(id->residences.remove(id));
		save();
		return remove.size();
	}
	
	@Override
	public abstract IDataProvider save();
	
	public IDataProvider close()
	{
		return close(true);
	}
	
	@Override
	public abstract IDataProvider close(boolean save);
	
	public IDataProvider reload()
	{
		return reload(false);
	}
	
	@Override
	public abstract IDataProvider reload(boolean save);
	
	@Override
	public abstract String getName();
}
