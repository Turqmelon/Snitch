![Logo](https://d3vv6lp55qjaqc.cloudfront.net/items/402j1g2B023f0d412u3K/GraphicText.png?X-CloudApp-Visitor-Id=1484866&v=247e27a7)

Snitch block logging and rollback.

**WARNING:** Snitch is still very much in development. We do not recommend using it on production servers at this time.

![Welcome](https://d3vv6lp55qjaqc.cloudfront.net/items/39292t0P011q1j2y1v3W/Screen%20Shot%202018-07-17%20at%209.30.58%20PM.png?X-CloudApp-Visitor-Id=1484866&v=749fdf45)

## Why Snitch?
Snitch is the only block logging and rollback plugin that was built on the APIs of 1.12. This ensures that events that are not caught by other plugins, such as armor stand interaction, item frames, and many other specific details are logged and rolled back properly.

## How does it compare?
We try to take the best features of many other popular plugins you've grown to love:

* Rollback previews from HawkEye
* A simple command structure from LogBlock
* Powerful and intuitive features from Prism

## How do I know that Snitch will be kept up to date?
Snitch is always going to be open source and community-first. We'll always be looking for available PRs to extend functionality and ensure that our users have the best experience possible.

## And 1.13?
We want to support 1.13 as soon as we can, but also realize that there are a lot of users who will continue to use 1.12. We hope to have 1.13 support as soon as possible.

## And 1.8?
There are no plans for 1.8 support at this time.

---
# Commands

Commands in Snitch are super easy to get the hang of. We'll cover the basics, first:

* `/snitch actions|a` provides an in-game reference for the available, searchable actions
* `/snitch params|p` provides an in-game reference of the different parameters available to you, with examples
* `/snitch rollback|rb <params>` performs a rollback using the provided parameters
* `/snitch restore|rs <params>` re-applies actions from a rollback. Think of it like an undo command
* `/snitch preview|pv <params>` provides a player-only preview of a rollback, making it simple to preview your changes before they happen
* `/snitch lookup|l <params>` performs a lookup using the specified parameters
* `/snitch near [radius]` is a shortcut for typing `/snitch l area 5`. Enter a different radius to search nearby within that radius
* `/snitch delete|del <params>` deletes records from the Snitch database
* `/snitch teleport|tp <#>` teleport to a record from a lookup. You can also just click on the record in your chat.
* `/snitch inspector|i` toggle the inspector.
* `/snitch next|prev` change pages in a lookup
* `/snitch page <#>` go to a specific page from a lookup
* `/snitch drain|dr [radius]` drain all liquids in the provided radius. Defaults to 10 if not specified.
* `/snitch extinguish|ex [radius]` extinguishes all fires in the provided radius. Defaults to 10 if not specified.

# Permissions

Snitch has very configurable permissions so you can give exactly what you want to each player or staff member.

* `snitch.actions` - Grants access to the **/snitch actions** command
* `snitch.params` - Grants access to the **/snitch params** command
* `snitch.rollback` - Grants access to the **/snitch rollback** command. _(This permission is not required to apply rollback previews.)_
* `snitch.restore` - Grants access to the **/snitch restore** command. _(This permission is not required to undo a messed up rollback.)_
* `snitch.delete` - Grants access to the **/snitch delete** command. _(DANGEROUS!)_
* `snitch.preview` - Grants access to the **/snitch preview** command. _(Also grants the user access to perform rollbacks via /snitch pv apply.)_
* `snitch.lookup` - Grants access to the **/snitch lookup** command. This permission also grants access to **/snitch next**, **/snitch prev** and **/snitch page**.
* `snitch.near` - Grants access to the **/snitch near** command. _(This will not grant them /snitch lookup. Near is a much more restricted lookup view.)_
* `snitch.teleport` - Grants access to the **/snitch teleport** command, and the ability to click on entries.
* `snitch.inspector` - Grants access to the **/snitch inspector** command.
* `snitch.drain` - Grants access to the **/snitch drain** command.
* `snitch.extinguish` - Grants access to the **/snitch ex** command.
* `snitch.undo` - Grants access to the **/snitch undo** command, to quickly reverse a restore or rollback
* `snitch.actions.all` - Grants access to ALL actions to use within commands the user has access to
* `snitch.action.<action>` - Grants access to a specific action to use within commands the user has access to. _(View the list of actions in /snitch actions)_
* `snitch.range.global` - Allows the user to not specify a range for their activites. If they don't have permission, then a range MUST be provided.
* `snitch.viewdata.ip` - Allows users to see IP addresses of players if you log player joins
* `snitch.tool` - Permits the user to use Snitch inspector tools
* `snitch.notify` - Allows the user to receive notifications for rollbacks and restores

---
# Using Snitch
If you've ever used a logging or rollback plugin before, learning Snitch will be a snap. There are no complicated syntax or symbol formats to remember, just the data you want to search by.

## Hello, Inspector.

![Inspector](https://d3vv6lp55qjaqc.cloudfront.net/items/3X1m0s0B0I213e421L2v/Screen%20Shot%202018-07-17%20at%209.23.36%20PM.png?X-CloudApp-Visitor-Id=1484866&v=da8ec191)

The **Snitch Inspector** is used for single-block investigations, and requires no tools like other plugins may. Toggle the inspector quickly by using `/s i`.

To look into a block that was broken, right click where it once was.
To look into a block that was placed, just punch it.

You'll get quick records in chat informing you of the activity for that space. Turn off the inspector with the same command to continue on your business.

## Nearby Investigation

![Near](https://d3vv6lp55qjaqc.cloudfront.net/items/0B0R1d2i3R31042B1V2v/Screen%20Shot%202018-07-17%20at%209.25.06%20PM.png?X-CloudApp-Visitor-Id=1484866&v=b4be1fe2)
Sometimes one block isn't enough to tell you the whole story. Use `/s near` to give you a history of all activity for within 5 blocks of you. You can also optionally use `/s near #` to change this radius to something larger, like 20 blocks.

Like the inspector, you'll get a chat popup for everything that's happened.

## Specifics Specifics
Sometimes you want to get really specific with your criteria. The near command may have provided you the name of the troublemaker, but how do we find out what else they may have caused?

![Specific 1](https://d3vv6lp55qjaqc.cloudfront.net/items/2U3R1q0B3o2V0E0W3431/Screen%20Shot%202018-07-17%20at%209.26.00%20PM.png?X-CloudApp-Visitor-Id=1484866&v=4a194de8)
Let's look up all their activity for the past day:
`/s l player SomeGriefer from 1d`

There are a few ways I could type that as well. Instead of `player`, I could say `p` or instead of `from` I could say `since` or just `s`. Syntax isn't a major concern when performing lookups.

![Specific 2](https://d3vv6lp55qjaqc.cloudfront.net/items/0m3u2B1c273u2p064206/Screen%20Shot%202018-07-17%20at%209.26.54%20PM.png?X-CloudApp-Visitor-Id=1484866&v=b203455b)
_Crossed out entries indicate an action that's been rolled back.)_

Maybe he had an accomplice that we want to include:
`/s l p SomeGriefer SomeBaddie s 1d`

You can include multiple bits of data just by typing them out. We also used the one-letter versions in that command to save time.

## Teleporting to the Damage

![Teleport](https://d3vv6lp55qjaqc.cloudfront.net/items/0031260U2B2X3A0u3H2s/Screen%20Shot%202018-07-17%20at%209.28.07%20PM.png?X-CloudApp-Visitor-Id=1484866&v=27ffbc59)
We found what we're looking for, but it may be a bit far. Luckily, Snitch makes teleporting to specific events a breeze.

* When viewing records, a dark gray ID will be shown to the far right of the entry. This is the **Record Index**. To teleport to it, simply type **/snitch tp <#>**.
* Alternatively, you can also just click on the record in chat.

## It's like it never happened.
Snitch performs near-perfect rollbacks of the damaged area. We do this by logging almost all attrbitures about blocks and entities that are destroyed, removed, or otherwise broken.

### Rollback Previews
You can preview the effect of a rollback before applying it to the server by using `/snitch preview`. When you preview a rollback, only the block changes are shown to you. Other adjustments, like killed monsters or displaced entities may not be reflected by a rollback preview. Say we wanted to preview the rollback for our criteria above:

![Preview](https://d3vv6lp55qjaqc.cloudfront.net/items/1h3s1e3t061F1r471x2g/Screen%20Shot%202018-07-17%20at%209.29.04%20PM.png?X-CloudApp-Visitor-Id=1484866&v=118376a9)
`/snitch pv player SomeGriefer SomeBaddie since 1d`

Snitch will give you a glance at what it'll look like. From there, you can type either `/snitch pv apply` to convert those changes to an actual rollback, or `/snitch pv cancel` to cancel the visualization. You can also perform another preview request with altered criteria to alter what you see.

### Making the Rollback
From a preview, you can simply use `/snitch pv apply` to apply the rollback, but if you're confident in your abilities, you can make the rollback directly. Using the same criteria above, we'll use the following command:

![Rollback](https://d3vv6lp55qjaqc.cloudfront.net/items/273s2H0D0c1W191f1P1A/Screen%20Shot%202018-07-17%20at%209.29.31%20PM.png?X-CloudApp-Visitor-Id=1484866&v=204ef764)
`/snitch rb player SomeGriefer SomeBaddie since 1d`

Without any further confirmation, the rollback will commence and the damage will be reverted.

## Re-Applying World Changes
Uh oh, you accidentally reverted something you shouldn't have! Luckily, Snitch maintains records _even after you've rolled them back_, just marked off specifically. Let's say **SomeBaddie** built a nice house near our spawn area we want to keep. We cna run the following command:

![Restore](https://d3vv6lp55qjaqc.cloudfront.net/items/2s1f3h0C062i3K1B353H/Screen%20Shot%202018-07-17%20at%209.30.00%20PM.png?X-CloudApp-Visitor-Id=1484866&v=fea33fe1)
`/snitch restore player SomeBaddie area 20`

Snitch will re-apply the world changes to the specific area and mark the logs as non-reverted for future use. Lovely!

We can also do this if we don't want to travel directly to the area, if we know the coordinates. Modifying the original command slightly...

`/snitch restore player SomeBaddie area 20 relative 100 150 100`

It's like the rollback never happened there!

## Deleting Unnecessary Records

### Auto-Clean
In the Snitch configuration, you can turn on **Auto-Clean** to automatically purge your server of old records each time your server starts up. It's important to have proper autoclean rules defined to keep Snitch running as fast and efficiently as possible.

By default, Snitch will delete all records older then 1 year, and water/lava flow data older than 7 days.

The configuration looks something like this:

```yml
autoclean:
  enable: true
  actions:
  - 'before 365d'
  - 'actions flow before 7d'
```

Auto-Clean actions use the same parameters that you use for lookups and rollbacks. Simply specify the filters for the data you'd like to delete. You can even limit the amount of data deleted per task by specifying `limit x` next to any of your clean tasks.

### Manually

You can also delete data yourself in-game using the `/snitch delete` command. 

Say I wanted to delete ALL history of liquid flowing COMPLETELY. I can do that with the simple command: `/snitch delete a flow`.

Snitch will do a quick check on how many records this will affect and make sure I know what I'm doing:
![Manual Delete](https://d3vv6lp55qjaqc.cloudfront.net/items/391n2I3Q0q1A1W3r1v01/Screen%20Shot%202018-08-06%20at%207.30.06%20PM.png?X-CloudApp-Visitor-Id=1484866&v=b7f67583)

You can see that even with the dangerous command I typed, Snitch auto-completed to only delete flow records for the past 3 days. (This is the default time search as specified in the `config.yml`). I can override this by specifying `since` myself, but I'll leave it as the 3 day default.

If I'm sure, I just type `/snitch delete confirm` and the matching records will be deleted.

![Delete Success](https://d3vv6lp55qjaqc.cloudfront.net/items/0o0i0C0t3t3N2H1X0c2l/Screen%20Shot%202018-08-06%20at%207.31.40%20PM.png?X-CloudApp-Visitor-Id=1484866&v=c4ed3b94)

**IMPORTANT:** The `/snitch delete` command performs a TRUE delete. There is no way to recover the data that was removed using it, so it's important to only give this command to users you trust to not do your server harm.


---
# Actions
Actions are a list of what's logged by Snitch. By default, all actions are enabled but can be specifically disabled by listing them in the `config.yml` file.

![Actions](https://d3vv6lp55qjaqc.cloudfront.net/items/0E3L1n3X3G3F0y1s3F2s/Screen%20Shot%202018-07-17%20at%209.30.26%20PM.png?X-CloudApp-Visitor-Id=1484866&v=58f7984d)
You can view a list of logged actions in-game using `/snitch actions`.

---
# Parameters
Parameters (or params) are the different criteria you can specify in rollback, restore, and lookup commands. Mastering what you can specify is key to ensuring accurate and correct rollbacks.

## Actor
The **Actor** parameter allows you to specify changes done by a specific player, entity, or block.

* Aliases: `player`, `players`, `p`, or `actor`
* Accepts: One or multiple entries
* Examples: `Turqmelon`, `S-TNT`, `S-Enderman`

* `/snitch l player Turqmelon`
* `/snitch l actor S-TNT`
* `/snitch l players Turqmelon JustPants Rhonim`

### Snitch Actors
Snitch maintains an internal player list used for frequent logs. We prefix these actor names with `S-` as to not collide with any real MC names.

Say you wanted to rollback enderman griefing, it'd be as easy as `/snitch rb area 20 player S-Enderman`.

## Action
The **Action** parameters allows you to narrow down your activities by specific things players can do.

* Aliases: `action`, `actions`, `a`
* Accepts: One or multiple entries
* Examples: `break`, `explode`, `chat`, `block_explode`

* `/snitch l action burn`
* `/snitch l action chat`
* `/snitch l player Turqmelon action break`

## Since
The **Since** parameter allows you to narrow down your results to happening _after_ a specific time.

* Aliases: `since`, `from`, `s`
* Accepts: A specific date or a relative time
* Examples: `07/01/18`, `1d`, `30m`

* `/snitch l player Turqmelon since 10m`
* `/snitch l action chat since 1d`

## Before 
The **Before** parameter allows you to narrow down your results to happening _before_ a specific time.

* Aliases: `before`, `prior`, `b`
* Accepts: A specific date or relative time
* Examples: `07/01/18`, `1d`, `30m`

* `/snitch l player Turqmelon since 7d before 6d`
* `/snitch l action break before 07/02/18 since 06/30/18`

## World
The **World** parameter allows you to reference a specific world. If you leave this out, it'll default to searching globally. (If you specify the Range paramater, this will be automatically set as your current world.)

* Aliases: `world`, `w`
* Accepts: A single world
* Examples: `world_nether`

* `/snitch l player Turqmelon world world_nether`
* `/snitch l action explode world skyworld`

## Radius
The **Radius** parameter filters down your search to a specific area. If you don't specify a location with the **Coords** param, your own location will be used, and if you don't specify a **World**, your own world will be used. Don't specify this parameter for a global lookup.

* Aliases: `radius`, `range`, `area`
* Accepts: A single number
* Examples: `5`, `20`

* `/snitch l area 20`
* `/snitch l player Turqmelon area 50`
* `/snitch l action burn range 5`

## Coords
The **Coords** parameter allows you to use a location other then your own as the lookup point. This is primarily used with the **Range** param to search places you're not.

* Aliases: `coords`, `relative`, `pos`, `position`
* Accepts: A set of coordinates
* Examples: `100 150 100`

* `/snitch l area 20 relative 100 150 100 world skyworld`

