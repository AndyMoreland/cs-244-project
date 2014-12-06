d6withcrypto <- read.csv("data/6withcrypto")
d6withoutcrypto <- read.csv("data/6withoutcrypto")
d8withcrypto <- read.csv("data/8withcrypto")
d8withoutcrypto <- read.csv("data/8withoutcrypto")
d10withcrypto <- read.csv("data/10withcrypto")
d10withoutcrypto <- read.csv("data/10withoutcrypto")
d12withcrypto <- read.csv("data/12withcrypto")
d12withoutcrypto <- read.csv("data/12withoutcrypto")
d14withcrypto <- read.csv("data/14withcrypto")
d14withoutcrypto <- read.csv("data/14withoutcrypto")
d16withcrypto <- read.csv("data/16withcrypto")
d16withoutcrypto <- read.csv("data/16withoutcrypto")


predicate = d6withcrypto < 4500
d6withcrypto <- d6withcrypto[predicate]

predicate <- d6withoutcrypto < 4500
d6withoutcrypto <- d6withoutcrypto[predicate]

predicate <- d8withcrypto < 4500
d8withcrypto <- d8withcrypto[predicate]

predicate <- d8withoutcrypto < 4500
d8withoutcrypto <- d8withoutcrypto[predicate]

predicate <- d10withcrypto < 4500
d10withcrypto <- d10withcrypto[predicate]

predicate <- d10withoutcrypto < 4500
d10withoutcrypto <- d10withoutcrypto[predicate]

predicate <- d12withcrypto < 4500
d12withcrypto <- d12withcrypto[predicate]

predicate <- d12withoutcrypto < 4500
d12withoutcrypto <- d12withoutcrypto[predicate]

predicate <- d14withcrypto < 4500
d14withcrypto <- d14withcrypto[predicate]

predicate <- d14withoutcrypto < 4500
d14withoutcrypto <- d14withoutcrypto[predicate]

predicate <- d16withcrypto < 4500
d16withcrypto <- d16withcrypto[predicate]

predicate <- d16withoutcrypto < 4500
d16withoutcrypto <- d16withoutcrypto[predicate]





datawithoutcrypto <- data.frame(d6withcrypto)

d6withcryptoaverage <- mean(d6withcrypto)
d8withcryptoaverage <- mean(d8withcrypto)
d10withcryptoaverage <- mean(d10withcrypto)
d12withcryptoaverage <- mean(d12withcrypto)
d14withcryptoaverage <- mean(d14withcrypto)
d16withcryptoaverage <- mean(d16withcrypto)

d6withoutcryptoaverage <- mean(d6withoutcrypto)
d8withoutcryptoaverage <- mean(d8withoutcrypto)
d10withoutcryptoaverage <- mean(d10withoutcrypto)
d12withoutcryptoaverage <- mean(d12withoutcrypto)
d14withoutcryptoaverage <- mean(d14withoutcrypto)
d16withoutcryptoaverage <- mean(d16withoutcrypto)

replicaCounts <- c(6,8,10,12,14,16)

withCryptoAverages <- c(d6withcryptoaverage, d8withcryptoaverage, d10withcryptoaverage, d12withcryptoaverage, d14withcryptoaverage, d16withcryptoaverage)

withoutCryptoAverages <- c(d6withoutcryptoaverage, d8withoutcryptoaverage, d10withoutcryptoaverage, d12withoutcryptoaverage, d14withoutcryptoaverage, d16withoutcryptoaverage)

d <- data.frame(replicaCounts, withCryptoAverages, withoutCryptoAverages)

quadlm <- lm(d$withCryptoAverages ~ I(d$replicaCounts^2) + d$replicaCounts)
quadlm2 <- lm(d$withoutCryptoAverages ~ I(d$replicaCounts^2) + d$replicaCounts)

png("timevsreplicacount.png")
plot(d$replicaCounts, d$withCryptoAverages, col="red", ylim=c(0, 1000), xlab="Number of Replicas in Cluster", ylab="Average Time for Move Commit")
title("Average Time for Move Commit vs. Replica Count")
points(d$replicaCounts, d$withoutCryptoAverages, col="blue")
lines(d$replicaCounts, d$withoutCryptoAverages, col="blue")
lines(d$replicaCounts, predict(quadlm, data.frame(x=d$replicaCounts)), col="red")
# lines(d$replicaCounts, predict(quadlm2, data.frame(x=d$replicaCounts)))
legend(x="right", legend=c("With Signatures", "Without Signatures"), col=c("red", "blue"), pch=15)

dev.off()

dev.new()
png("timevssequencenumber.png")
t <- seq(1, length(d12withcrypto))
lm <- lm(d12withcrypto ~ t)

plot(t, d12withcrypto, xlab="Sequence Number", ylab="Time for Move Commit", ylim=c(0, 1000))
title("Time for Move Commit vs. Sequence Number")
lines(t, predict(lm, data.frame(x=t)))
dev.off()
