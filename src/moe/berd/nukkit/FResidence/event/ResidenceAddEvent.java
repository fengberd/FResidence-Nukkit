package moe.berd.nukkit.FResidence.event;

import cn.nukkit.*;
import cn.nukkit.level.*;

public class ResidenceAddEvent extends CancellableFResidenceEvent
{
	private String name;
	private double money;
	private Player player;
	private Position select1,select2;
	
	public ResidenceAddEvent(double money,Position select1,Position select2,String name,Player player)
	{
		super(null);
		this.name=name;
		this.money=money;
		this.player=player;
		this.select1=select1;
		this.select2=select2;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public Position getPos1()
	{
		return select1;
	}
	
	public void setPos1(Position val)
	{
		this.select1=val;
	}
	
	public Position getPos2()
	{
		return select2;
	}
	
	public void setPos2(Position val)
	{
		this.select2=val;
	}
	
	public String getResName()
	{
		return name;
	}
	
	public void setResName(String name)
	{
		this.name=name;
	}
	
	public double getMoney()
	{
		return money;
	}
	
	public void setMoney(double money)
	{
		this.money=money;
	}
}
