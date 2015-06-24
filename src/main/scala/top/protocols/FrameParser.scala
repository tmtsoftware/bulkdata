package top.protocols

import java.nio.ByteOrder

import akka.stream.stage.{Context, PushPullStage, SyncDirective}
import akka.util.ByteString

class FrameParser(implicit order: ByteOrder) extends PushPullStage[ByteString, ByteString] {

   // this holds the received but not yet parsed bytes
   var stash = ByteString.empty
   // this holds the current message length or -1 if at a boundary
   var needed = -1

   override def onPush(bytes: ByteString, ctx: Context[ByteString]) = {
     stash ++= bytes
     run(ctx)
   }
   override def onPull(ctx: Context[ByteString]) = run(ctx)
   override def onUpstreamFinish(ctx: Context[ByteString]) =
     if (stash.isEmpty) ctx.finish()
     else ctx.absorbTermination() // we still have bytes to emit

   private def run(ctx: Context[ByteString]): SyncDirective =
     if (needed == -1) {
       // are we at a boundary? then figure out next length
       if (stash.length < 4) pullOrFinish(ctx)
       else {
         needed = stash.iterator.getInt
         stash = stash.drop(4)
         run(ctx) // cycle back to possibly already emit the next chunk
       }
     } else if (stash.length < needed) {
       // we are in the middle of a message, need more bytes
       pullOrFinish(ctx)
     } else {
       // we have enough to emit at least one message, so do it
       val emit = stash.take(needed)
       stash = stash.drop(needed)
       needed = -1
       ctx.push(emit)
     }

   /*
    * After having called absorbTermination() we cannot pull any more, so if we need
    * more data we will just have to give up.
    */
   private def pullOrFinish(ctx: Context[ByteString]) =
     if (ctx.isFinishing) ctx.finish()
     else ctx.pull()
 }
