package wicket.authorization.strategies.role.metadata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import wicket.authorization.Action;
import wicket.authorization.strategies.role.Roles;

/**
 * For each Action, holds a set of roles that can perform that action. Roles can
 * be granted access to a given action via authorize(Action, String role) and
 * denied access via unauthorize(Action, String role). All permissions can be
 * removed for a given action via authorizeAll(Action).
 * 
 * @author Eelco Hillenius
 * @author Jonathan Locke
 */
final class ActionPermissions implements Serializable
{
	private static final long serialVersionUID = 1L;

	/** Map from an action to a set of role strings */
	private final Map<Action, Roles> rolesForAction = new HashMap<Action, Roles>();

	/**
	 * Gives permission for the given roles to perform the given action
	 * 
	 * @param action
	 *            The action
	 * @param rolesToAdd
	 *            The roles
	 */
	public final void authorize(final Action action, final Roles rolesToAdd)
	{
		// FIXME Documentation: What is this magic looking code here for?
		rolesForAction.put(new Action("")
		{
			private static final long serialVersionUID = 1L;
		}, null);
		
		if (action == null)
		{
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		if (rolesToAdd == null)
		{
			throw new IllegalArgumentException("Argument rolesToAdd cannot be null");
		}

		Roles roles = rolesForAction.get(action);
		if (roles == null)
		{
			roles = new Roles();
			rolesForAction.put(action, roles);
		}
		roles.addAll(rolesToAdd);
	}

	/**
	 * Remove all authorization for the given action.
	 * 
	 * @param action
	 *            The action to clear
	 */
	public final void authorizeAll(final Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		rolesForAction.remove(action);
	}

	/**
	 * Gets the roles that have a binding for the given action.
	 * 
	 * @param action
	 *            The action
	 * @return The roles authorized for the given action
	 */
	public final Roles rolesFor(final Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		return rolesForAction.get(action);
	}

	/**
	 * Remove the given authorized role from an action.
	 * 
	 * @param action
	 *            The action
	 * @param rolesToRemove
	 *            The comma separated list of roles to remove
	 */
	public final void unauthorize(final Action action, final Roles rolesToRemove)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Argument action cannot be null");
		}

		if (rolesToRemove == null)
		{
			throw new IllegalArgumentException("Argument rolesToRemove cannot be null");
		}

		Roles roles = rolesForAction.get(action);
		if (roles != null)
		{
			roles.removeAll(rolesToRemove);
		}

		// If we removed the last authorized role, we authorize the empty role
		// so that removing authorization can't suddenly open something up to
		// everyone.
		if (roles.size() == 0)
		{
			roles.add(MetaDataRoleAuthorizationStrategy.NO_ROLE);
		}
	}
}
