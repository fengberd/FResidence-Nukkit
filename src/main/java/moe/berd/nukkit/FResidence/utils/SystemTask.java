package moe.berd.nukkit.FResidence.utils;

import cn.nukkit.scheduler.*;

import moe.berd.nukkit.FResidence.*;

public class SystemTask extends PluginTask<Main>
{
	public SystemTask(Main owner)
	{
		super(owner);
	}
	
	@Override
	public void onRun(int currentTick)
	{
		getOwner().systemTaskCallback(currentTick);
	}
}
