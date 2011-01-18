/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ProcessReachedQuestAchievementsAction;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPDependentOnLevelAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction;
import games.stendhal.server.entity.npc.action.SayRequiredItemAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * QUEST: Daily Item Fetch Quest.
 * <p>
 * PARTICIPANTS:
 * <li> Mayor of Ados
 * <li> some items
 * <p>
 * STEPS:
 * <li> talk to Mayor of Ados to get a quest to fetch an item
 * <li> bring the item to the mayor
 * <li> if you cannot bring it in one week he offers you the chance to fetch
 * another instead
 * <p>
 * REWARD:
 * <li> xp 
 * <li> 10 Karma
 * <p>
 * REPETITIONS:
 * <li> once a day
 */
public class DailyItemQuest extends AbstractQuest {

	private static final String QUEST_SLOT = "daily_item";
	
	/** How long until the player can give up and start another quest */
	private static final int expireDelay = MathHelper.MINUTES_IN_ONE_WEEK; 
	
	/** How often the quest may be repeated */
	private static final int delay = MathHelper.MINUTES_IN_ONE_DAY; 
	
	/**
	 * All items which are possible/easy enough to find. If you want to do
	 * it better, go ahead. *
	 * not to use yet, just getting it ready.
	 */
	private static Map<String,Integer> items;

	private static void buildItemsMap() {
		items = new HashMap<String, Integer>();
		items.put("knife",1);
		items.put("dagger",1);
		items.put("short sword",1);
		items.put("sword",1);
		items.put("scimitar",1);
		items.put("katana",1);
		items.put("claymore",1);
		items.put("broadsword",1);
		items.put("biting sword",1);
		items.put("old scythe",1);
		items.put("small axe",1);
		items.put("hand axe",1);
		items.put("axe",1);
		items.put("battle axe",1);
		items.put("bardiche",1);
		items.put("scythe",1);
		items.put("twoside axe",1);
		items.put("halberd",1);
		items.put("club",1);
		items.put("staff",1);
		items.put("mace",1);
		items.put("flail",1);
		items.put("morning star",1);
		items.put("hammer",1);
		items.put("war hammer",1);
		items.put("wooden bow",1);
		items.put("longbow",1);
		items.put("wooden arrow",1);
		items.put("steel arrow",1);
		items.put("buckler",1);
		items.put("wooden shield",1);
		items.put("studded shield",1);
		items.put("plate shield",1);
		items.put("lion shield",1);
		items.put("unicorn shield",1);
		items.put("skull shield",1);
		items.put("crown shield",1);
		items.put("dress",1);
		items.put("leather armor",1);
		items.put("leather cuirass",1);
		items.put("studded armor",1);
		items.put("chain armor",1);
		items.put("scale armor",1);
		items.put("plate armor",1);
		items.put("leather helmet",1);
		items.put("studded helmet",1);
		items.put("chain helmet",1);
		items.put("leather legs",1);
		items.put("studded legs",1);
		items.put("chain legs",1);
		items.put("leather boots",1);
		items.put("studded boots",1);
		items.put("cloak",1);
		items.put("elf cloak",1);
		items.put("dwarf cloak",1);
		items.put("green dragon cloak",1);
		items.put("cheese",10);
		items.put("carrot",10);
		items.put("salad",10);
		items.put("apple",5);
		items.put("bread",5);
		items.put("meat",10);
		items.put("ham",10);
		items.put("sandwich",5);
		items.put("pie",5);
		items.put("egg",1);
		items.put("button mushroom",10);
		items.put("porcini",10);
		items.put("toadstool",15);
		items.put("beer",10);
		items.put("wine",10);
		items.put("minor potion",5);
		items.put("antidote",5);
		items.put("greater antidote",5);
		items.put("potion",5);
		items.put("greater potion",5);
		items.put("poison",5);
		items.put("flask",5);
		items.put("money",100);
		items.put("arandula",5);
		items.put("wood",10);
		items.put("grain",20);
		items.put("flour",5);
		items.put("iron ore",10);
		items.put("iron",5);
		items.put("dice",1);
		items.put("teddy",1);
		items.put("perch",5);
		items.put("roach",5);
		items.put("char",5);
		items.put("trout",5);
		items.put("surgeonfish",5);
		items.put("onion",5);
		items.put("leek",5);
		items.put("clownfish",5);
		items.put("leather scale armor",1);
		items.put("pauldroned leather cuirass",1);
		items.put("enhanced chainmail",1);
		items.put("iron scale armor",1);
		items.put("golden chainmail",1);
		items.put("pauldroned iron cuirass",1);
		items.put("blue elf cloak",1);
		items.put("enhanced mace",1);
		items.put("golden mace",1);
		items.put("golden hammer",1);
		items.put("aventail",1);
		items.put("composite bow",1);
		items.put("enhanced lion shield",1);
		items.put("spinach",5);
		items.put("courgette",5);
		items.put("collard",5);
		items.put("coal",10);
		items.put("pick",1);
		items.put("grilled steak",1);
	}
	
	private void getQuest() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new NotCondition(new TimePassedCondition(QUEST_SLOT,1,expireDelay))), 
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You're already on a quest to fetch [item]"
						+ ". Say #complete if you brought it!"));
		
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new TimePassedCondition(QUEST_SLOT,1,expireDelay)), 
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You're already on a quest to fetch [item]"
						+ ". Say #complete if you brought it! Perhaps there are no supplies of that left at all! You could fetch #another item if you like, or return with what I first asked you."));
	
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
								 new NotCondition(new TimePassedCondition(QUEST_SLOT,1,delay))), 
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT,1, delay, "I can only give you a new quest once a day. Please check back in"));
		
		
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new StartRecordingRandomItemCollectionAction(QUEST_SLOT,0,items,"Ados is in need of supplies. Go fetch [item]"
				+ " and say #complete, once you've brought it."));	
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(new QuestNotStartedCondition(QUEST_SLOT),
								new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
												 new TimePassedCondition(QUEST_SLOT,1,delay))), 
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(actions));
	}
	
	private void completeQuest() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"I'm afraid I didn't send you on a #quest yet.",
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES, 
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"You already completed the last quest I had given to you.",
				null);
		
		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new DropRecordedItemAction(QUEST_SLOT,0));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		actions.add(new IncrementQuestAction(QUEST_SLOT, 2, 1));
		actions.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		actions.add(new IncreaseXPDependentOnLevelAction(8, 90.0));
		actions.add(new IncreaseKarmaAction(10.0));
		actions.add(new ProcessReachedQuestAchievementsAction());
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,0)),
				ConversationStates.ATTENDING, 
				"Good work! Let me thank you on behalf of the people of Ados!",
				new MultipleActions(actions));
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,0))),
				ConversationStates.ATTENDING, 
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You didn't fetch [item]"
						+ " yet. Go and get it and say #complete only once you're done."));
		
	}
	
	private void abortQuest() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
						 		 new TimePassedCondition(QUEST_SLOT,1,expireDelay)), 
				ConversationStates.ATTENDING, 
				"I see. Please, ask me for another #quest when you think you can help Ados again.", 
				new SetQuestAction(QUEST_SLOT, 0, "done"));
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
						 		 new NotCondition(new TimePassedCondition(QUEST_SLOT,1,expireDelay))), 
				ConversationStates.ATTENDING, 
				"It hasn't been long since you've started your quest, I won't let you give up so soon.", 
				null);
		
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new QuestNotActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, 
				"I'm afraid I didn't send you on a #quest yet.", 
				null);
		
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have met Mayor Chalmers in Ados Townhall.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not want to help Ados.");
			return res;
		}

		res.add("I want to help Ados.");
		if (player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)) {
			String questItem = player.getRequiredItemName(QUEST_SLOT,0);
			int amount = player.getRequiredItemQuantity(QUEST_SLOT,0);
			if (!player.isEquipped(questItem, amount)) {
				res.add(("I have been asked to fetch "
						+ Grammar.quantityplnoun(amount, questItem, "a") + " to help Ados. I haven't got it yet."));
			} else {
				res.add(("I have found "
						+ Grammar.quantityplnoun(amount, questItem, "a") + " to help Ados and need to take it."));
			}
		}
		int repetitions = getNumberOfRepetitions(player);
		if (repetitions > 0) {
			res.add("I helped Ados with supplies "
					+ Grammar.quantityplnoun(repetitions, "time") + " so far.");
		}
		if (isRepeatable(player)) {
			res.add("I fetched the last item the mayor asked me to find and now Ados needs supplies again.");
		} else if (isCompleted(player)){
			res.add("I fetched the last item the mayor asked me to find and claimed my reward within the last 24 hours.");
		}
		return res;
	}
	

	@Override
	public int getNumberOfRepetitions(Player player) {
		String questState = player.getQuest(getSlotName(), 2);
		return MathHelper.parseIntDefault(questState, 0);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Daily Item Quest",
				"Mayor Chalmers needs supplies for Ados City.",
				true);
		
		buildItemsMap();
		
		getQuest();
		completeQuest();
		abortQuest();
	}

	@Override
	public String getName() {
		return "DailyItemQuest";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}
	
	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,delay)).fire(player, null, null);
	}
}
