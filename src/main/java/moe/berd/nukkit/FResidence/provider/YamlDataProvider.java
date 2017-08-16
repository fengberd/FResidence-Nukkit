package moe.berd.nukkit.FResidence.provider;

import java.io.*;
import java.util.*;

import cn.nukkit.utils.*;

import moe.berd.nukkit.FResidence.*;
import moe.berd.nukkit.FResidence.utils.*;
import moe.berd.nukkit.FResidence.utils.Utils;
import moe.berd.nukkit.FResidence.exception.*;

public class YamlDataProvider extends BaseDataProvider
{
	private Config config;
	
	public YamlDataProvider(Main main)
	{
		
		super(main);
	}
	
	public Config getConfig()
	{
		return config;
	}
	
	@Override
	public IDataProvider save()
	{
		List<ConfigSection> data=new ArrayList<>();
		residences.values().forEach(res->data.add(res.getRawData()));
		config.setAll(new ConfigSection()
		{
			{
				set("DataVersion",Utils.CONFIG_VERSION);
				set("Residences",data);
			}
		});
		config.save();
		return this;
	}
	
	@Override
	public IDataProvider close(boolean save)
	{
		if(save)
		{
			save();
		}
		config=null;
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public IDataProvider reload(boolean save)
	{
		if(save)
		{
			save();
		}
		config=new Config(new File(main.getDataFolder(),"residence.yml").toString(),Config.YAML,new ConfigSection()
		{
			{
				set("DataVersion",Utils.CONFIG_VERSION);
				set("Residences",new ArrayList<ConfigSection>());
			}
		});
		try
		{
			config.set("Residences",Utils.updateConfig(config.getInt("DataVersion"),config.getList("Residences")));
		}
		catch(VersionException e)
		{
			main.getLogger().error("配置文件更新错误",e);
		}
		config.save();
		residences.clear();
		for(Object o : config.getList("Residences"))
		{
			ConfigSection res=new ConfigSection((LinkedHashMap<String,Object>)o);
			try
			{
				if(getResidenceByName(res.getString("name"))!=null)
				{
					main.getLogger().warning("加载领地 "+res.getString("name")+" 时出现异常:存在同名领地");
				}
				else
				{
					int id=residences.size();
					residences.put(id,new Residence(this,id,res));
				}
			}
			catch(FResidenceException e)
			{
				main.getLogger().warning("加载领地 "+res.getString("name")+" 时出现错误",e);
				// TODO:添加failed变量,处理一下世界未加载的情况
			}
		}
		return this;
	}
	
	@Override
	public String getName()
	{
		return "Yaml";
	}
}
