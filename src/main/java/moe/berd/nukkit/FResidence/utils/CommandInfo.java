package moe.berd.nukkit.FResidence.utils;

public class CommandInfo
{
	public int argsCount;
	public String command, usage, description;
	
	public CommandInfo(int argsCount,String command,String category)
	{
		this.argsCount=argsCount;
		this.command=command;
		this.usage="commands."+category+"."+command+".usage";
		this.description="commands."+category+"."+command+".description";
	}
}
