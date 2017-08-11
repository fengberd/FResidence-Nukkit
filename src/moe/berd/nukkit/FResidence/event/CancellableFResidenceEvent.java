package moe.berd.nukkit.FResidence.event;

import cn.nukkit.event.*;

import moe.berd.nukkit.FResidence.utils.Residence;

public class CancellableFResidenceEvent extends FResidenceEvent implements Cancellable
{
	public CancellableFResidenceEvent(Residence res)
	{
		super(res);
	}
}
