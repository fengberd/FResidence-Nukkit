package moe.berd.nukkit.FResidence.event;

import moe.berd.nukkit.FResidence.utils.*;

public class ResidenceOwnerChangeEvent extends CancellableFResidenceEvent
{
	private String owner;
	
	public ResidenceOwnerChangeEvent(Residence res,Object owner)
	{
		super(res);
		this.owner=Utils.getPlayerName(owner);
	}
	
	public String getOwner()
	{
		return owner;
	}
	
	public void setOwner(Object val)
	{
		owner=Utils.getPlayerName(owner);
	}
}
