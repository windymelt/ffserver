package dev.capslock.ffserver

import os.Path
import cats.effect.IO
import io.github.windymelt.qw.Syntax.qw

object FFMpeg {
  def convert(input: Path): IO[Path] = IO.blocking {
    val outfile = s"${input}.mkv"
    val cmd = qw"""
ffmpeg -y -i ${input.toString}
-map 0:v -map 0:a
-r 30
-vf bwdif,scale=1280:-2 -max_muxing_queue_size 999999
-c:v libsvtav1 -crf 25 -preset 8
-c:a libopus -ar 48000 -b:a 128k
-strict -2
$outfile
"""

    val result =
      os.proc(cmd).call(cwd = os.pwd, stdin = os.Inherit) // TODO: redirect 1>&2
    result.exitCode match {
      case 0 =>
        os.remove(input)
        Path(outfile)
      case _ => throw new Exception("ffmpeg failed")
    }
  }
}
