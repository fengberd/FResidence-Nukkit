package moe.berd.nukkit.FResidence.command;

import cn.nukkit.utils.*;
import cn.nukkit.command.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.exception.*;
import moe.berd.nukkit.FResidence.utils.Utils;

public class ResidenceCommand extends Command
{
	private Main main;
	
	public ResidenceCommand(Main main)
	{
		super("residence",Utils.translate("command.residence.description"),Utils.translate("command.residence.usage"),new String[]{"res"});
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
				return this.main.onResidenceCommand(sender,args);
			}
			catch(FResidenceException e)
			{
				sender.sendMessage(Utils.getColoredString("exception.cannot_perform_action",TextFormat.RED,e.getMessage()));
			}
		}
		return true;
	}
}
