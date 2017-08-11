package moe.berd.nukkit.FResidence.event;

import cn.nukkit.event.*;

import moe.berd.nukkit.FResidence.utils.*;

public class FResidenceEvent extends Event
{
	protected Residence res;
	
	public FResidenceEvent(Residence res)
	{
		this.res=res;
	}
	
	public Residence getRes()
	{
		return res;
	}
}
