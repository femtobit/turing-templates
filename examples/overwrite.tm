symbols = {a, b, c};
states  = {0, 1, 2};
start   = 0;
end     = {2};

transitions = {
    (0,a) -> (a,0,R),
    (0,b) -> (a,0,R),
    (0,c) -> (a,0,R),
    (0,#) -> (#,1,L),
    (1,a) -> (a,1,L),
    (1,#) -> (#,2,R)
};

