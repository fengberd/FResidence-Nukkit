package moe.berd.nukkit.FResidence.utils;

import java.util.*;

public class CommandCollection extends HashMap<String,CommandInfo>
{
	public String category;
	
	public CommandCollection(String category)
	{
		super();
		this.category=category;
	}
	
	public CommandCollection addCommand(String command,int argsCount)
	{
		put(command,new CommandInfo(argsCount,command,category));
		return this;
	}
}
