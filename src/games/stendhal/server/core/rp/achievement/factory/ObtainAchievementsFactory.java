package games.stendhal.server.core.rp.achievement.factory;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerHasObtainedNumberOfItemsFromWellGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * factory for item related achievements.
 *
 * @author madmetzger
 */
public class ObtainAchievementsFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.OBTAIN;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> achievements = new LinkedList<Achievement>();
		achievements.add(createAchievement("obtain.wish", "A wish came true", "Get an item from the wishing well",
				Achievement.EASY_BASE_SCORE, new PlayerHasObtainedNumberOfItemsFromWellGreaterThanCondition(0)));
		return achievements;
	}

}
