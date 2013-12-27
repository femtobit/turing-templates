states = {running, finished};
symbols = {a};
start = running;
end = {finished};

transitions = {
    (running, a) -> (#, running, right),
    (running, #) -> (#, finished, left)
};

