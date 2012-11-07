1. Do not use getString(key, default) of Storage! It rewrites quests file, if no such key in the quests YAML!
However, other getXXX(key, default) are safe

Modified classes in net.citizensnpcs.questers:
    QuestType
    api.QuestAPI
    quests.types.ChatQuests - moved