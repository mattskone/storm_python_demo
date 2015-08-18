import random
import string
import time

import storm


class TestSpout(storm.Spout):

    def nextTuple(self):
        """Emit a random letter every five seconds."""

        time.sleep(5)
        storm.emit([random.choice(string.ascii_letters)])


TestSpout().run()
