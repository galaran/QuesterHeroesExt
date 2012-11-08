QuesterHeroesExt
================

Heroes support and some other changes for Citizens1 Quester

#### Warning

Do not use `getString(key, default)` of Storage! It forces to resave quests file, if no such key in the quests YAML!
However, other `getXXX(key, default)` are safe
