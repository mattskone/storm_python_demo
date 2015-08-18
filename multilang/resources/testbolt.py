import storm


class TestBolt(storm.BasicBolt):

    def process(self, tup):
        """Double whatever value was passed in."""

        storm.emit([tup.values[0] * 2])


TestBolt().run()
