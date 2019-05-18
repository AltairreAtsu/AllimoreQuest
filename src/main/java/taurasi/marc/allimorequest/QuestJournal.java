package taurasi.marc.allimorequest;

import org.bukkit.configuration.file.FileConfiguration;
import taurasi.marc.allimorequest.Config.ConfigWrapper;
import taurasi.marc.allimorequest.Utils.NullableInt;
import taurasi.marc.allimorequest.Utils.QuestLogger;

import java.util.ArrayList;

public class QuestJournal {
    private static int MAX_QUESTS_PER_JOURNAL = 3;

    private PlayerQuestData playerData;
    private Quest[] quests;

    // Constructors
    public QuestJournal(PlayerQuestData playerData){
        this.playerData = playerData;
        quests = new Quest[MAX_QUESTS_PER_JOURNAL];
    }
    public QuestJournal(FileConfiguration config, String path, PlayerQuestData playerData){
        quests = new Quest[MAX_QUESTS_PER_JOURNAL];
        this.playerData = playerData;

        for(int i = 0; i < MAX_QUESTS_PER_JOURNAL; i++){
            if ( !(config.contains(String.format("%sQuests.Slot %o.Name", path, i))) ) continue;
            quests[i] = new Quest(config, String.format("%sQuests.Slot %o.", path, i), playerData);
        }
    }

    public void WriteToConfig(FileConfiguration config, String section){
        for (int i = 0; i < quests.length; i++){
            if(quests[i] == null) continue;
            quests[i].WriteToConfig(config, String.format("%s.Quests.Slot %o.", section, i));
        }
    }

    // Array Manipulation
    public boolean AddQuestToJournal(Quest quest){
        NullableInt index = FirstEmpty();
        if(index == null){
            return false;
        }

        quests[index.value] = quest;
        return true;
    }
    public void RemoveQuestFromJournal(Quest quest){
        for(int i = 0; i < quests.length; i++){
            if( (quests[i] != null) && (quest == quests[i]) ){
                quests[i] = null;
                return;
            }
        }
        QuestLogger.LogError("Could not find quest in quest journal! Player:" + playerData.GetPlayer().getName());
    }
    public void RemoveQuestFromJournal(String name){
        Quest quest = Find(name);
        if(quest == null) return;

        RemoveQuestFromJournal(quest);
    }
    public Quest Find(String name){
        for(int i = 0; i < quests.length; i++){
            if( (quests[i] != null) && (quests[i].GetQuestName().equalsIgnoreCase(name)) ){
                return quests[i];
            }
        }
        QuestLogger.LogError(ConfigWrapper.INFO_CANNOT_FIND_QUEST, playerData.GetOnlinePlayer());
        return null;
    }
    private NullableInt FirstEmpty(){
        for(int i = 0; i < quests.length; i++){
            if(quests[i] == null) return new NullableInt(i);
        }
        return null;
    }

    public Quest[] GetQuests(){
        return quests;
    }
    public ArrayList<String> GetQuestNames(){
        ArrayList<String> questNames = new ArrayList<String>(MAX_QUESTS_PER_JOURNAL);
        for(int i = 0; i < quests.length; i++){
            if(quests[i] == null) continue;
            questNames.add(quests[i].GetQuestName());
        }

        return questNames;
    }

    public boolean TrySendQuestsToChat(){
        boolean empty = true;

        for(int i = 0; i < quests.length ; i++){
            if(quests[i] == null) continue;
            quests[i].notificationService.DisplayQuestBriefInChat(playerData.GetOnlinePlayer());
            empty = false;
        }

        return empty;
    }
}
