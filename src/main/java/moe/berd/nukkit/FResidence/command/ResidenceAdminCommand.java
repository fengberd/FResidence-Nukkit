package moe.berd.nukkit.FResidence.command;

import cn.nukkit.utils.*;
import cn.nukkit.command.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.exception.*;
import moe.berd.nukkit.FResidence.utils.Utils;

public class ResidenceAdminCommand extends Command
{
	private Main main;
	
	public ResidenceAdminCommand(Main main)
	{
		super("residenceadmin",Utils.translate("command.residenceadmin.description"),"",new String[]{"resadmin"});
		this.main=main;
		this.setPermission("FResidence.admin");
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
				this.main.onResidenceAdminCommand(sender,args);
			}
			catch(FResidenceException e)
			{
				sender.sendMessage(Utils.getColoredString("exception.cannot_perform_action",TextFormat.RED,e.getMessage()));
			}
		}
		return true;
	}
}
