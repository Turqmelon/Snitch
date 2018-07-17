# Snitch
Snitch block logging and rollback

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

Commands in snitch are super easy to get the hang of. We'll cover the basics, first:

* `/snitch actions|a` provides an in-game reference for the available, searchable actions
* `/snitch params|p` provides an in-game reference of the different parameters available to you, with examples
* `/snitch rollback|rb <params>` performs a rollback using the provided parameters
* `/snitch restore|rs <params>` re-applies actions from a rollback. Think of it like an undo command
* `/snitch preview|pv <params>` provides a player-only preview of a rollback, making it simple to preview your changes before they happen
* `/snitch lookup|l <params>` perform sa lookup using the specified parameters
* `/snitch near [radius]` is a shortcut for typing `/snitch l area 5`. Enter a different radius to search nearby for different radiuses
* `/snitch teleport|tp <#>` teleport to a record from a lookup. You can also just click on the record in your chat.
* `/snitch inspector|i` toggle the inspector.
* `/snitch next|prev` change pages in a lookup
* `/snitch page <#>` go to a specific page from a lookup
* `/snitch drain|dr [radius]` drain all liquids in the provided radius. Defaults to 10 if not specified.
* `/snitch extinguish|ex [radius]` extinguishes all fires in the provided radius. Defaults to 10 if not specified.

---
# Using Snitch
If you've ever used a logging or rollback plugin before, learning Snitch will be a snap. There are no complicated syntax or symbol formats to remember, just the data you want to search by.

## Hello, Inspector.
The **Snitch Inspector** is used for single-block investigations, and requires no tools like other plugins may. Toggle the inspector quickly by using `/s i`.

To look into a block that was broken, right click where it once was.
To look into a block that was placed, just punch it.

You'll get quick records in chat informing you of the activity for that space. Turn off the inspector with the same command to continue on your business.

## Nearby Investigation
Sometimes one block isn't enough to tell you the whole story. Use `/s near` to give you a history of all activity for within 5 blocks of you. You can also optionally use `/s near #` to change this radius to something larger, like 20 blocks.

Like the inspector, you'll get a chat popup for everything that's happened.

## Specifics Specifics
Sometimes you want to get really specific with your criteria. The near command may have provided you the name of the troublemaker, but how do we find out what else they may have caused?

Let's look up all their activity for the past day:
`/s l player SomeGriefer from 1d`

There are a few ways I could type that as well. Instead of `player`, I could say `p` or instead of `from` I could say `since` or just `s`. Syntax isn't a major concern when performing lookups.

Maybe he had an accomplice that we want to include:
`/s l p SomeGriefer SomeBaddie s 1d`

You can include multiple bits of data just by typing them out. We also used the one-letter versions in that command to save time.

## Teleporting to the Damage
We found what we're looking for. 
