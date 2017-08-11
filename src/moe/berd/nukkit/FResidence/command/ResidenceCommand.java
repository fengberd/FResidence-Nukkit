package moe.berd.nukkit.FResidence.command;

import cn.nukkit.command.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.utils.*;
import moe.berd.nukkit.FResidence.exception.*;

public class ResidenceCommand extends Command
{
	private Main main;
	
	public ResidenceCommand(Main main)
	{
		super("residence","领地插件指令 - 使用 /res help 查看帮助","使用 /res help 查看帮助",new String[]{"res"});
		this.main=main;
		this.setPermission("FResidence.command.residence");
	}
	
	@Override
	public boolean execute(CommandSender sender,String label,String[] args)
	{
		if(this.testPermission(sender))
		{
			try
			{
				if(args.length==0)
				{
					args=new String[]{"help"};
				}
				args[0]=args[0].toLowerCase();
				this.main.onResidenceCommand(sender,args);
			}
			catch(FResidenceException e)
			{
				sender.sendMessage(Utils.getRedString("无法完成操作: "+e.getMessage()));
			}
		}
		return true;
	}
}
