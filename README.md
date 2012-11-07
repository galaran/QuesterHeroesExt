QuesterHeroesExt
================

Heroes support and some other changes for Citizens1 Quester

Modified classes in net.citizensnpcs.questers:

* QuestType
* api.QuestAPI
* quests.types.ChatQuests - moved and rewrited


#### Warning

Do not use `getString(key, default)` of Storage! It forces to resave quests file, if no such key in the quests YAML!
However, other `getXXX(key, default)` are safe
